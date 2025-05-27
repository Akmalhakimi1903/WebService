package com.cal;

import java.io.IOException;
import java.io.PrintWriter;
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

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String name = request.getParameter("name");
            String ic = request.getParameter("ic");
            String gender = request.getParameter("gender");
            String weightStr = request.getParameter("weight");
            String heightStr = request.getParameter("height");
            String action = request.getParameter("action"); // To differentiate buttons

            boolean formSubmitted = name != null && ic != null && gender != null
                    && weightStr != null && heightStr != null && action != null;

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Personal Health & Fitness Calculator</title>");
            out.println("</head>");
            out.println("<body>");

            // Form
            out.println("<form method='post' action='Client'>");

            out.println("Name: <input type='text' name='name' value='" + (name != null ? name : "") + "' required><br><br>");
            out.println("IC Number: <input type='text' name='ic' value='" + (ic != null ? ic : "") + "' maxlength='12' pattern='\\d{12}' title='Enter a valid 12-digit IC number' required><br><br>");

            out.println("Gender: <select name='gender' required>");
            out.println("<option value=''>-- Select Gender --</option>");
            out.println("<option value='male'" + ("male".equalsIgnoreCase(gender) ? " selected" : "") + ">Male</option>");
            out.println("<option value='female'" + ("female".equalsIgnoreCase(gender) ? " selected" : "") + ">Female</option>");
            out.println("</select><br><br>");

            out.println("Weight (kg): <input type='number' step='0.1' name='weight' value='" + (weightStr != null ? weightStr : "") + "' required><br><br>");
            out.println("Height (cm): <input type='number' step='0.1' name='height' value='" + (heightStr != null ? heightStr : "") + "' required><br><br>");

            out.println("<input type='submit' name='action' value='Calculate BMI'>");
            out.println("<input type='submit' name='action' value='Calculate BFP'>");
            out.println("</form><br>");

            // Process results
            if (formSubmitted) {
                try {
                    int age = getAgeFromIC(ic);
                    double weight = Double.parseDouble(weightStr);
                    double height = Double.parseDouble(heightStr);
                    double heightM = height / 100.0;

                    double bmi = weight / (heightM * heightM);
                    bmi = Math.round(bmi * 100.0) / 100.0;

                    out.println("<h3>Result:</h3>");
                    out.println("<p>Name: " + name + "</p>");
                    out.println("<p>IC Number: " + ic + "</p>");
                    out.println("<p>Gender: " + gender + "</p>");
                    out.println("<p>Age (calculated): " + age + "</p>");
                    out.println("<p>Weight: " + weight + " kg</p>");
                    out.println("<p>Height: " + height + " cm</p>");
                    out.println("<h3>Your BMI is: " + bmi + "</h3>");

                    if ("Calculate BFP".equals(action)) {
                        double bfp;
                        if (gender.equalsIgnoreCase("male")) {
                            bfp = 1.20 * bmi + 0.23 * age - 16.2;
                        } else if (gender.equalsIgnoreCase("female")) {
                            bfp = 1.20 * bmi + 0.23 * age - 5.4;
                        } else {
                            throw new IllegalArgumentException("Invalid gender");
                        }
                        bfp = Math.round(bfp * 100.0) / 100.0;
                        out.println("<h3>Your Body Fat Percentage is: " + bfp + "%</h3>");
                    }

                } catch (NumberFormatException e) {
                    out.println("<p style='color:red;'>Invalid numeric input.</p>");
                } catch (IllegalArgumentException e) {
                    out.println("<p style='color:red;'>" + e.getMessage() + "</p>");
                }
            }

            out.println("</body>");
            out.println("</html>");
        }
    }

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
        return "Personal Health & Fitness Calculator";
    }
}
