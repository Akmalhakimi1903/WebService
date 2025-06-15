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
            if (name == null || name.trim().isEmpty()) {
                throwSOAPFault("Name cannot be empty");
            }

            if (icNumber == null || icNumber.trim().isEmpty()) {
                throwSOAPFault("IC number cannot be empty");
            }

            validateGender(gender);

            double weightKg = parsePositiveDouble(weightKgStr, "Weight");

            double heightCm = parsePositiveDouble(heightCmStr, "Height");

            validateActivityLevel(activityLevel);

            int age = getAgeFromIC(icNumber);

            double bmi = calculateBMI(weightKg, heightCm);
            double bfp = calculateBodyFat(gender, age, bmi);
            double bmr = calculateBMR(gender, age, weightKg, heightCm, activityLevel);
            String bmrClassification = classifyBMR(bmr);

            return "Hello, " + name + "!<br>"
                    + "IC Number: " + icNumber + "<br>"
                    + "Gender: " + gender + "<br>"
                    + "Age: " + age + "<br>"
                    + String.format("Weight: %.2f kg<br>", weightKg)
                    + String.format("Height: %.2f cm<br>", heightCm)
                    + String.format("Your Basal Metabolic Rate (BMR) adjusted for activity level is: <strong>%.2f kcal/day</strong> (%s)<br>", bmr, bmrClassification)
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

    @WebMethod(operationName = "calculateWaterIntake")
    public double calculateWaterIntake(
            @WebParam(name = "weight") String weightStr,
            @WebParam(name = "activityDuration") String activityDurationStr) {
        try {
            double weight = parsePositiveDouble(weightStr, "Weight");
            int activityDuration = parseNonNegativeInt(activityDurationStr, "Activity Duration");
            double baseWater = weight * 0.033;
            double activityWater = (activityDuration / 30.0) * 0.35;
            double totalWater = baseWater + activityWater;
            return Math.round(totalWater * 100.0) / 100.0;
        } catch (SOAPFaultException e) {
            throw e;
        } catch (Exception e) {
            throwSOAPFault("Failed to calculate water intake: " + e.getMessage());
            return 0;
        }
    }

    @WebMethod(operationName = "calculateHydrationStatus")
    public double calculateHydrationStatus(
            @WebParam(name = "gender") String gender,
            @WebParam(name = "age") String ageStr,
            @WebParam(name = "weightKg") String weightKgStr,
            @WebParam(name = "heightCm") String heightCmStr) {
        try {
            validateGender(gender);
            int age = parsePositiveInt(ageStr, "Age");
            double weightKg = parsePositiveDouble(weightKgStr, "Weight");
            double heightCm = parsePositiveDouble(heightCmStr, "Height");

            double hydrationLiters;
            if ("male".equalsIgnoreCase(gender)) {
                hydrationLiters = 2.447 - (0.09516 * age) + (0.1074 * heightCm) + (0.3362 * weightKg);
            } else {
                hydrationLiters = (0.1069 * heightCm) + (0.2466 * weightKg) - 2.097;
            }

            hydrationLiters = Math.round(hydrationLiters * 100.0) / 100.0;
            return hydrationLiters;

        } catch (SOAPFaultException e) {
            throw e;
        } catch (Exception e) {
            throwSOAPFault("Failed to calculate hydration status: " + e.getMessage());
            return 0;
        }
    }

    private int parseNonNegativeInt(String value, String fieldName) {
        try {
            int num = Integer.parseInt(value);
            if (num < 0) {
                throwSOAPFault(fieldName + " must be greater than or equal to 0. Received: " + num);
            }
            return num;
        } catch (NumberFormatException e) {
            throwSOAPFault(fieldName + " must be a valid integer. Received: '" + value + "'");
            return 0;
        }
    }

    private double parsePositiveDouble(String value, String fieldName) {
        try {
            double num = Double.parseDouble(value);
            if (num <= 0) {
                throwSOAPFault(fieldName + " must be greater than 0. Received: " + num);
            }
            return num;
        } catch (NumberFormatException e) {
            throwSOAPFault(fieldName + " must be a valid number. Received: '" + value + "'");
            return 0;
        }
    }

    private int parsePositiveInt(String value, String fieldName) {
        try {
            int num = Integer.parseInt(value);
            if (num <= 0) {
                throwSOAPFault(fieldName + " must be greater than 0. Received: " + num);
            }
            return num;
        } catch (NumberFormatException e) {
            throwSOAPFault(fieldName + " must be a valid integer. Received: '" + value + "'");
            return 0;
        }
    }

    private void throwSOAPFault(String message) {
        try {
            SOAPFactory soapFactory = SOAPFactory.newInstance();
            SOAPFault fault = soapFactory.createFault(
                    message,
                    new QName("http://schemas.xmlsoap.org/soap/envelope/", "Client")
            );
            fault.setFaultString(message);
            throw new SOAPFaultException(fault);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SOAP fault: " + e.getMessage(), e);
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

    private double calculateBMI(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        return Math.round((weightKg / (heightM * heightM)) * 100.0) / 100.0;
    }

    private double calculateBodyFat(String gender, int age, double bmi) {
        double bfp;
        if ("male".equalsIgnoreCase(gender)) {
            bfp = 1.20 * bmi + 0.23 * age - 10.8;
        } else {
            bfp = 1.20 * bmi + 0.23 * age - 5.4;
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
                break;
        }

        return Math.round(bmr * multiplier * 100.0) / 100.0;
    }

    private String classifyBMR(double bmr) {
        if (bmr < 1200) {
            return "Very Low";
        } else if (bmr < 1600) {
            return "Low";
        } else if (bmr < 2000) {
            return "Norma";
        } else if (bmr < 2400) {
            return "High";
        } else {
            return "Very High";
        }
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
}
