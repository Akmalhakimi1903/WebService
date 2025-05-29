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

        // Read parameters from request
        String name = request.getParameter("name");
        String ic = request.getParameter("ic");
        String gender = request.getParameter("gender");
        String weightStr = request.getParameter("weight");
        String heightStr = request.getParameter("height");
        String systolicStr = request.getParameter("systolic");
        String diastolicStr = request.getParameter("diastolic");
        String activityMinutesStr = request.getParameter("activityMinutes");
        String action = request.getParameter("action");

        try {
            // Validate and parse inputs
            int age = getAgeFromIC(ic);

            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);

            String contextPath = request.getContextPath();

            switch (action) {
                case "Calculate BMI":
                    double bmi = calculateBMI(weight, height);
                    String redirectUrl = contextPath + "/BMI.html?"
                            + "name=" + encode(name)
                            + "&ic=" + encode(ic)
                            + "&age=" + age
                            + "&gender=" + encode(gender)
                            + "&weight=" + weight
                            + "&height=" + height
                            + "&bmi=" + bmi;
                    response.sendRedirect(redirectUrl);
                    return;

                case "Calculate BFP":
                    double bmiForBfp = calculateBMI(weight, height);
                    double bfp = calculateBodyFat(gender, age, bmiForBfp);
                    String bfpURL = contextPath + "/BFP.html?"
                            + "name=" + encode(name)
                            + "&ic=" + encode(ic)
                            + "&age=" + age
                            + "&gender=" + encode(gender)
                            + "&weight=" + weight
                            + "&height=" + height
                            + "&bfp=" + bfp;
                    response.sendRedirect(bfpURL);
                    return;

                case "Calculate Blood Pressure":
                    int systolic = 0;
                    int diastolic = 0;

                    if (systolicStr == null || systolicStr.isEmpty() || diastolicStr == null || diastolicStr.isEmpty()) {
                        String errUrl = contextPath + "/BloodPressure.html?error=" + encode("Please enter both systolic and diastolic values.");
                        response.sendRedirect(errUrl);
                        return;
                    }

                    try {
                        systolic = Integer.parseInt(systolicStr);
                        diastolic = Integer.parseInt(diastolicStr);
                    } catch (NumberFormatException e) {
                        String errUrl = contextPath + "/BloodPressure.html?error=" + encode("Systolic and diastolic must be numeric.");
                        response.sendRedirect(errUrl);
                        return;
                    }

                    // Inline classifyBloodPressure logic
                    double score = 0.6 * systolic + 0.4 * diastolic;
                    String classification;
                    if (score < 72) {
                        classification = "Low Blood Pressure (Hypotension)";
                    } else if (score < 104) {
                        classification = "Normal Blood Pressure";
                    } else if (score < 110) {
                        classification = "Elevated Blood Pressure";
                    } else if (score < 122) {
                        classification = "Hypertension Stage 1";
                    } else if (score <= 156) {
                        classification = "Hypertension Stage 2";
                    } else {
                        classification = "Hypertensive Crisis (Seek Emergency Care)";
                    }

                    String bpUrl = contextPath + "/BloodPressure.html?"
                            + "name=" + encode(name)
                            + "&ic=" + encode(ic)
                            + "&gender=" + encode(gender)
                            + "&age=" + age
                            + "&weight=" + weight
                            + "&height=" + height
                            + "&systolic=" + systolic
                            + "&diastolic=" + diastolic
                            + "&bp=" + encode(classification);

                    response.sendRedirect(bpUrl);
                    return;

                case "Calculate Calories Burn":
                    double bmr = calculateBMR(gender, age, weight, height);
                    String calUrl = contextPath + "/Calories.html?name=" + encode(name) + "&bmr=" + bmr;
                    response.sendRedirect(calUrl);
                    return;

                case "Calculate Water Intake":
                    int activityMinutes = 0;
                    if (activityMinutesStr != null && !activityMinutesStr.isEmpty()) {
                        activityMinutes = Integer.parseInt(activityMinutesStr);
                    }
                    double base = weight * 0.033;
                    double activity = (activityMinutes / 30.0) * 0.35;
                    double waterIntake = Math.round((base + activity) * 100.0) / 100.0;

                    String waterUrl = contextPath + "/WaterIntake.html?"
                            + "name=" + encode(name)
                            + "&ic=" + encode(ic)
                            + "&age=" + age
                            + "&gender=" + encode(gender)
                            + "&weight=" + weight
                            + "&height=" + height
                            + "&activityMinutes=" + activityMinutes
                            + "&waterIntake=" + waterIntake;
                    response.sendRedirect(waterUrl);
                    return;

                default:
                    response.sendRedirect(contextPath + "/error.html");
                    return;
            }

        } catch (NumberFormatException e) {
            String errUrl = request.getContextPath() + "/error.html?error=" + encode("Invalid numeric input.");
            response.sendRedirect(errUrl);
        } catch (IllegalArgumentException e) {
            String errUrl = request.getContextPath() + "/error.html?error=" + encode(e.getMessage());
            response.sendRedirect(errUrl);
        }
    }

    //ic
    private int getAgeFromIC(String ic) {
        if (ic == null || ic.length() < 2) {
            throw new IllegalArgumentException("Invalid IC number.");
        }
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
        double bmi = weightKg / (heightM * heightM);
        return Math.round(bmi * 100.0) / 100.0;
    }

    private double calculateBodyFat(String gender, int age, double bmi) {
        if ("male".equalsIgnoreCase(gender)) {
            return Math.round((1.20 * bmi + 0.23 * age - 16.2) * 100.0) / 100.0;
        } else if ("female".equalsIgnoreCase(gender)) {
            return Math.round((1.20 * bmi + 0.23 * age - 5.4) * 100.0) / 100.0;
        } else {
            throw new IllegalArgumentException("Invalid gender");
        }
    }

    private double calculateBMR(String gender, int age, double weightKg, double heightCm) {
        if ("male".equalsIgnoreCase(gender)) {
            return Math.round((10 * weightKg + 6.25 * heightCm - 5 * age + 5) * 100.0) / 100.0;
        } else if ("female".equalsIgnoreCase(gender)) {
            return Math.round((10 * weightKg + 6.25 * heightCm - 5 * age - 161) * 100.0) / 100.0;
        } else {
            throw new IllegalArgumentException("Invalid gender");
        }
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
        return "Personal Health & Fitness Calculator Servlet";
    }
}
