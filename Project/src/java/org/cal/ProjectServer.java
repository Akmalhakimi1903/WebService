package org.cal;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.namespace.QName;

@WebService(serviceName = "ProjectServer")
public class ProjectServer {

    @WebMethod(operationName = "userInformation")
    public String display(
            @WebParam(name = "name") String name,
            @WebParam(name = "icNumber") String icNumber,
            @WebParam(name = "gender") String gender,
            @WebParam(name = "weightKg") String weightKgStr,
            @WebParam(name = "heightCm") String heightCmStr,
            @WebParam(name = "activityLevel") String activityLevel
    ) {
        try {
            double weightKg = parsePositiveDouble(weightKgStr, "Weight");
            double heightCm = parsePositiveDouble(heightCmStr, "Height");

            int age = getAgeFromIC(icNumber);
            validateGender(gender);
            validateActivityLevel(activityLevel);

            double bmi = calculateBMI(weightKg, heightCm);
            double bfp = calculateBodyFat(gender, age, bmi);
            double bmr = calculateBMR(gender, age, weightKg, heightCm, activityLevel);

            return "Hello, " + name + "!<br>"
                    + "IC Number: " + icNumber + "<br>"
                    + "Gender: " + gender + "<br>"
                    + "Age: " + age + "<br>"
                    + String.format("Weight: %.2f kg<br>", weightKg)
                    + String.format("Height: %.2f cm<br>", heightCm)
                    + String.format("Your Basal Metabolic Rate (BMR) adjusted for activity level is: <strong>%.2f kcal/day</strong><br>", bmr)
                    + String.format("BMI: %.2f<br>", bmi)
                    + "Your Body Fat Percentage is: <strong>" + bfp + "%</strong><br>";
        } catch (SOAPFaultException e) {
            throw e;
        } catch (Exception e) {
            throwSOAPFault("Internal server error: " + e.getMessage());
            return null;
        }
    }

    @WebMethod
    public String classifyBloodPressure(
            @WebParam(name = "systolic") String systolicStr,
            @WebParam(name = "diastolic") String diastolicStr) {
        try {
            int systolic = parsePositiveInt(systolicStr, "Systolic");
            int diastolic = parsePositiveInt(diastolicStr, "Diastolic");
            return classifyBloodPressureInternal(systolic, diastolic);
        } catch (SOAPFaultException e) {
            throw e;
        } catch (Exception e) {
            throwSOAPFault("Internal server error: " + e.getMessage());
            return null;
        }
    }

    @WebMethod
    public double calculateWaterIntake(
            @WebParam(name = "weight") String weightStr,
            @WebParam(name = "activityMinutes") String activityMinutesStr) {
        try {
            double weight = parsePositiveDouble(weightStr, "Weight");
            int activityMinutes = parseNonNegativeInt(activityMinutesStr, "Activity Minutes");

            double base = weight * 0.033;
            double activity = (activityMinutes / 30.0) * 0.35;
            return Math.round((base + activity) * 100.0) / 100.0;
        } catch (SOAPFaultException e) {
            throw e;
        } catch (Exception e) {
            throwSOAPFault("Internal server error: " + e.getMessage());
            return 0;
        }
    }

    // ========== Validation Methods ==========
    private double parsePositiveDouble(String value, String fieldName) {
        try {
            double num = Double.parseDouble(value);
            if (num <= 0) {
                throwSOAPFault(fieldName + " must be a positive value greater than zero.");
            }
            return num;
        } catch (NumberFormatException e) {
            throwSOAPFault(fieldName + " must be a valid numeric value.");
            return 0; // unreachable
        }
    }

    private int parsePositiveInt(String value, String fieldName) {
        try {
            int num = Integer.parseInt(value);
            if (num <= 0) {
                throwSOAPFault(fieldName + " must be a positive integer.");
            }
            return num;
        } catch (NumberFormatException e) {
            throwSOAPFault(fieldName + " must be a valid integer.");
            return 0;
        }
    }

    private int parseNonNegativeInt(String value, String fieldName) {
        try {
            int num = Integer.parseInt(value);
            if (num < 0) {
                throwSOAPFault(fieldName + " cannot be negative.");
            }
            return num;
        } catch (NumberFormatException e) {
            throwSOAPFault(fieldName + " must be a valid integer.");
            return 0;
        }
    }

    private void validateGender(String gender) {
        if (gender == null || (!gender.equalsIgnoreCase("male") && !gender.equalsIgnoreCase("female"))) {
            throwSOAPFault("Gender must be 'male' or 'female'.");
        }
    }

    private void validateActivityLevel(String activityLevel) {
        if (activityLevel == null || activityLevel.trim().isEmpty()) {
            throwSOAPFault("Activity level cannot be empty.");
        }

        String lvl = activityLevel.toLowerCase();
        if (!lvl.equals("light") && !lvl.equals("moderate") && !lvl.equals("active")) {
            throwSOAPFault("Activity level must be one of: light, moderate, or active.");
        }
    }

    private int getAgeFromIC(String icNumber) {
        if (icNumber == null || icNumber.trim().isEmpty()) {
            throwSOAPFault("IC number cannot be empty.");
        }

        icNumber = icNumber.trim();

        if (icNumber.length() != 12 || !icNumber.matches("\\d{12}")) {
            throwSOAPFault("Invalid IC number format. IC must be exactly 12 digits.");
        }

        String yearPart = icNumber.substring(0, 2);
        int year = Integer.parseInt(yearPart);
        int currentYear = java.time.Year.now().getValue();

        if (year <= currentYear % 100) {
            year += 2000;
        } else {
            year += 1900;
        }

        return currentYear - year;
    }

    // ========== Calculation Methods ==========
    private double calculateBMI(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        return Math.round((weightKg / (heightM * heightM)) * 100.0) / 100.0;
    }

    private double calculateBodyFat(String gender, int age, double bmi) {
        double bfp;
        if ("male".equalsIgnoreCase(gender)) {
            bfp = 1.20 * bmi + 0.23 * age - 10.8; // Correct Deurenberg formula for males
        } else {
            bfp = 1.20 * bmi + 0.23 * age - 5.4;  // Correct Deurenberg formula for females
        }
        return Math.round(bfp * 100.0) / 100.0;
    }

    private double calculateBMR(String gender, int age, double weightKg, double heightCm, String activityLevel) {
        double bmr;
        if ("male".equalsIgnoreCase(gender)) {
            bmr = 10 * weightKg + 6.25 * heightCm - 5 * age + 5;
        } else {
            bmr = 10 * weightKg + 6.25 * heightCm - 5 * age - 161;
        }

        double multiplier;
        switch (activityLevel.toLowerCase()) {
            case "light":
                multiplier = 1.375;
                break;
            case "moderate":
                multiplier = 1.55;
                break;
            case "active":
                multiplier = 1.725;
                break;
            default:
                multiplier = 1.2;
                break; // This path is unreachable if validation is enforced
        }

        return Math.round(bmr * multiplier * 100.0) / 100.0;
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

    // SOAP Fault helper
    private void throwSOAPFault(String message) {
        try {
            SOAPFactory soapFactory = SOAPFactory.newInstance();
            SOAPFault fault = soapFactory.createFault(
                    message,
                    new QName("http://schemas.xmlsoap.org/soap/envelope/", "Client")
            );
            throw new SOAPFaultException(fault);
        } catch (Exception e) {
            throw new RuntimeException("Failed to throw SOAP fault", e);
        }
    }
}
