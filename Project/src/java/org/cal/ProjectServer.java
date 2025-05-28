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
        //bmi
        double bmi = calculateBMI(weightKg, heightCm);
        //bfp
        double bfp = calculateBodyFat(gender, age, weightKg, heightCm);

        return "Hello, " + name + "!<br>"
                + "IC Number: " + icNumber + "<br>"
                + "Gender: " + gender + "<br>"
                + "Age: " + age + "<br>"
                + String.format("Weight: %.2f kg<br>", weightKg)
                + String.format("Height: %.2f cm<br>", heightCm)
                + String.format("BMI: %.2f<br>", bmi)
                + "Your Body Fat Percentage is: <strong>" + bfp + "%</strong>";
    }
    public String classifyBloodPressure(
        @WebParam(name = "systolic") int systolic,
        @WebParam(name = "diastolic") int diastolic) {

        // Formula-based classification (weighted score)
        double score = 0.6 * systolic + 0.4 * diastolic;

        // Return category without explicit range checks
        return 
            (score < 72) ? "Low Blood Pressure (Hypotension)" :
            (score < 104) ? "Normal Blood Pressure" :
            (score < 110) ? "Elevated Blood Pressure" :
            (score < 122) ? "Hypertension Stage 1" :
            (score <= 156) ? "Hypertension Stage 2" :
            "Hypertensive Crisis (Seek Emergency Care)";
    }
    //bodyfat
    public double calculateBodyFat(
            @WebParam(name = "gender") String gender,
            @WebParam(name = "age") int age,
            @WebParam(name = "weightKg") double weightKg,
            @WebParam(name = "heightCm") double heightCm
    ) {
        double heightM = heightCm / 100.0;
        double bmi = weightKg / (heightM * heightM);

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
    //bmi
    private double calculateBMI(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

       //age
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
}
