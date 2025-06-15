package com.cal;

import java.io.IOException;
import java.time.Year;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "Client", urlPatterns = {"/Client"})
public class Client extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String name = request.getParameter("name");
        String ic = request.getParameter("ic");
        String gender = request.getParameter("gender");
        String weightStr = request.getParameter("weight");
        String heightStr = request.getParameter("height");
        String systolicStr = request.getParameter("systolic");
        String diastolicStr = request.getParameter("diastolic");
        String activityMinutesStr = request.getParameter("activityMinutes");
        String activityLevel = request.getParameter("activityLevel");
        String action = request.getParameter("action");

        int age = getAgeFromIC(ic);
        double weight = Double.parseDouble(weightStr);
        double height = Double.parseDouble(heightStr);
        String contextPath = request.getContextPath();

        switch (action) {
            case "Calculate BMI":
                double bmi = calculateBMI(weight, height);
                response.sendRedirect(contextPath + "/BMI.html?"
                        + "name=" + encode(name)
                        + "&ic=" + encode(ic)
                        + "&age=" + age
                        + "&gender=" + encode(gender)
                        + "&weight=" + weight
                        + "&height=" + height
                        + "&bmi=" + bmi);
                return;

            case "Calculate BFP":
                double bmiForBfp = calculateBMI(weight, height);
                double bfp = calculateBodyFat(gender, age, bmiForBfp);
                response.sendRedirect(contextPath + "/BFP.html?"
                        + "name=" + encode(name)
                        + "&ic=" + encode(ic)
                        + "&age=" + age
                        + "&gender=" + encode(gender)
                        + "&weight=" + weight
                        + "&height=" + height
                        + "&bfp=" + bfp);
                return;

            case "Calculate Blood Pressure":
                response.sendRedirect(contextPath + "/BloodPressure.html?"
                        + "name=" + encode(name)
                        + "&ic=" + encode(ic)
                        + "&gender=" + encode(gender)
                        + "&age=" + age
                        + "&weight=" + weight
                        + "&height=" + height);
                return;

            case "Calculate Calories Burn":
                double bmr = calculateBMR(gender, age, weight, height, activityLevel);
                String bmrClass = classifyBMR(bmr);
                response.sendRedirect(contextPath + "/CaloriesBurn.html?"
                        + "name=" + encode(name)
                        + "&ic=" + encode(ic)
                        + "&age=" + age
                        + "&gender=" + encode(gender)
                        + "&weight=" + weight
                        + "&height=" + height
                        + "&activityLevel=" + encode(activityLevel != null ? activityLevel : "")
                        + "&bmr=" + bmr
                        + "&bmrClass=" + encode(bmrClass));
                return;

            case "Calculate Water Intake":
                int activityMinutes = 0;
                if (activityMinutesStr != null && !activityMinutesStr.isEmpty()) {
                    activityMinutes = Integer.parseInt(activityMinutesStr);
                }

                double base = weight * 0.033;
                double activity = (activityMinutes / 30.0) * 0.35;
                double waterIntake = Math.round((base + activity) * 100.0) / 100.0;

                response.sendRedirect(contextPath + "/WaterIntake.html?"
                        + "name=" + encode(name)
                        + "&ic=" + encode(ic)
                        + "&age=" + age
                        + "&gender=" + encode(gender)
                        + "&weight=" + weight
                        + "&height=" + height
                        + "&activityMinutes=" + activityMinutes
                        + "&waterIntake=" + waterIntake);
                return;

            case "Calculate Hydration Status":
                double hydration = calculateHydrationStatus(gender, age, weight, height);
                response.sendRedirect(contextPath + "/HydrationStatus.html?"
                        + "name=" + encode(name)
                        + "&ic=" + encode(ic)
                        + "&age=" + age
                        + "&gender=" + encode(gender)
                        + "&weight=" + weight
                        + "&height=" + height
                        + "&hydration=" + hydration);
                return;

            default:
                response.sendRedirect(contextPath + "/index.html");
        }
    }

    private int getAgeFromIC(String ic) {
        String yearStr = ic.substring(0, 2);
        int year = Integer.parseInt(yearStr);
        int currentYear = Year.now().getValue();

        if (year <= currentYear % 100) {
            year += 2000;
        } else {
            year += 1900;
        }
        return currentYear - year;
    }

    private double calculateBMI(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        return Math.round((weightKg / (heightM * heightM)) * 100.0) / 100.0;
    }

    private double calculateBodyFat(String gender, int age, double bmi) {
        if ("male".equalsIgnoreCase(gender)) {
            return Math.round((1.20 * bmi + 0.23 * age - 16.2) * 100.0) / 100.0;
        } else if ("female".equalsIgnoreCase(gender)) {
            return Math.round((1.20 * bmi + 0.23 * age - 5.4) * 100.0) / 100.0;
        } else {
            return 0.0;
        }
    }

    private double calculateBMR(String gender, int age, double weightKg, double heightCm, String activityLevel) {
        double bmr;

        if ("male".equalsIgnoreCase(gender)) {
            bmr = 10 * weightKg + 6.25 * heightCm - 5 * age + 5;
        } else if ("female".equalsIgnoreCase(gender)) {
            bmr = 10 * weightKg + 6.25 * heightCm - 5 * age - 161;
        } else {
            bmr = 0.0;
        }

        double multiplier = 1.2;
        if (activityLevel != null) {
            switch (activityLevel.toLowerCase()) {
                case "light": multiplier = 1.375; break;
                case "moderate": multiplier = 1.55; break;
                case "active": multiplier = 1.725; break;
            }
        }
        double adjustedBMR = bmr * multiplier;
        return Math.round(adjustedBMR * 100.0) / 100.0;
    }

    private String classifyBMR(double bmr) {
        if (bmr < 1000) {
            return "Extremely Low";
        } else if (bmr < 1200) {
            return "Very Low";
        } else if (bmr < 1400) {
            return "Low";
        } else if (bmr < 1600) {
            return "Below Average";
        } else if (bmr < 1800) {
            return "Average";
        } else if (bmr < 2000) {
            return "Above Average";
        } else if (bmr < 2200) {
            return "High";
        } else if (bmr < 2400) {
            return "Very High";
        } else {
            return "Extremely High";
        }
    }

    private double calculateHydrationStatus(String gender, int age, double weightKg, double heightCm) {
        double hydrationLiters;
        if ("male".equalsIgnoreCase(gender)) {
            hydrationLiters = 2.447 - (0.09516 * age) + (0.1074 * heightCm) + (0.3362 * weightKg);
        } else {
            hydrationLiters = (0.1069 * heightCm) + (0.2466 * weightKg) - 2.097;
        }
        return Math.round(hydrationLiters * 100.0) / 100.0;
    }

    private String encode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Client Servlet for Health & Fitness Calculations (with Hydration Status)";
    }
}
