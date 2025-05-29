package org.cal;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

@WebService(serviceName = "ProjectServer")
public class ProjectServer {

    @WebMethod(operationName = "userInformation")
    public String display(
            @WebParam(name = "name") String name,
            @WebParam(name = "icNumber") String icNumber,
            @WebParam(name = "gender") String gender,
            @WebParam(name = "weightKg") double weightKg,
            @WebParam(name = "heightCm") double heightCm
    ) {
        int age = getAgeFromIC(icNumber);
        double bmi = calculateBMI(weightKg, heightCm);
        double bfp = calculateBodyFat(gender, age, weightKg, heightCm);
        double bmr = calculateBMR(gender, age, weightKg, heightCm);

        return "Hello, " + name + "!<br>"
                + "IC Number: " + icNumber + "<br>"
                + "Gender: " + gender + "<br>"
                + "Age: " + age + "<br>"
                + String.format("Weight: %.2f kg<br>", weightKg)
                + String.format("Height: %.2f cm<br>", heightCm)
                + String.format("BMI: %.2f<br>", bmi)
                + "Your Body Fat Percentage is: <strong>" + bfp + "%</strong><br>"
                + String.format("Your Basal Metabolic Rate (BMR) is: <strong>%.2f kcal/day</strong>", bmr);
    }

   
    public String classifyBloodPressure(
            @WebParam(name = "systolic") int systolic,
            @WebParam(name = "diastolic") int diastolic) {
        return classifyBloodPressureInternal(systolic, diastolic);
    }

    // ========== Helper methods below ==========
    private int getAgeFromIC(String icNumber) {
        if (icNumber == null || icNumber.length() < 2) {
            throw new IllegalArgumentException("Invalid IC number.");
        }

        int currentYear = java.time.Year.now().getValue();
        String yearPart = icNumber.substring(0, 2);
        int year = Integer.parseInt(yearPart);

        if (year <= currentYear % 100) {
            year += 2000;
        } else {
            year += 1900;
        }

        return currentYear - year;
    }

    private double calculateBMI(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    private double calculateBodyFat(String gender, int age, double weightKg, double heightCm) {
        double bmi = calculateBMI(weightKg, heightCm);
        double bfp;

        if (gender.equalsIgnoreCase("male")) {
            bfp = 1.20 * bmi + 0.23 * age - 16.2;
        } else if (gender.equalsIgnoreCase("female")) {
            bfp = 1.20 * bmi + 0.23 * age - 5.4;
        } else {
            throw new IllegalArgumentException("Gender must be 'male' or 'female'");
        }

        return Math.round(bfp * 100.0) / 100.0;
    }

    private double calculateBMR(String gender, int age, double weightKg, double heightCm) {
        double bmr;

        if (gender.equalsIgnoreCase("male")) {
            bmr = 10 * weightKg + 6.25 * heightCm - 5 * age + 5;
        } else if (gender.equalsIgnoreCase("female")) {
            bmr = 10 * weightKg + 6.25 * heightCm - 5 * age - 161;
        } else {
            throw new IllegalArgumentException("Gender must be 'male' or 'female'");
        }

        return Math.round(bmr * 100.0) / 100.0;
    }

    private String classifyBloodPressureInternal(int systolic, int diastolic) {
        double score = 0.6 * systolic + 0.4 * diastolic;

        if (score < 72) {
            return "Low Blood Pressure (Hypotension)";
        } else if (score < 104) {
            return "Normal Blood Pressure";
        } else if (score < 110) {
            return "Elevated Blood Pressure";
        } else if (score < 122) {
            return "Hypertension Stage 1";
        } else if (score <= 156) {
            return "Hypertension Stage 2";
        } else {
            return "Hypertensive Crisis (Seek Emergency Care)";
        }
    }

    @WebMethod
    public double calculateWaterIntake(@WebParam(name = "weight") double weight,
            @WebParam(name = "activityMinutes") int activityMinutes) {
        double base = weight * 0.033;
        double activity = (activityMinutes / 30.0) * 0.35;
        return Math.round((base + activity) * 100.0) / 100.0;
    }
}
