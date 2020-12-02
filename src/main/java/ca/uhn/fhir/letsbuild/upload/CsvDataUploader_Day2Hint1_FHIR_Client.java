package ca.uhn.fhir.letsbuild.upload;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import com.google.common.base.Charsets;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.SimpleQuantity;

public class CsvDataUploader_Day2Hint1_FHIR_Client {

    public static void main(String[] args) throws Exception {

        // The following code creates a FHIR client - You can create one client and reuse it
        // for all requests.
        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/fhir");

        // This line adds a "logging interceptor" which causes the client to output some details
        // about its actions to the console
        client.registerInterceptor(new LoggingInterceptor(false));

        // Open the CSV file for reading
        try (InputStream inputStream = new FileInputStream("src/main/resources/sample-data.csv")) {
            Reader reader = new InputStreamReader(inputStream, Charsets.UTF_8);

            CSVFormat format = CSVFormat.EXCEL
                .withFirstRecordAsHeader()
                .withDelimiter(',');
            CSVParser csvParser = format.parse(reader);

            // Loop throw each row in the CSV file
            for (CSVRecord nextRecord : csvParser.getRecords()) {

                // Sequence number - This could be used as an ID for generated resources
                String seqN = nextRecord.get("SEQN");

                // Timestamp - This will be formatted in ISO8601 format
                String timestamp = nextRecord.get("TIMESTAMP");

                // Patient ID
                String patientId = nextRecord.get("PATIENT_ID");

                // Patient Family Name
                String patientFamilyName = nextRecord.get("PATIENT_FAMILYNAME");

                // Patient Given Name
                String patientGivenName = nextRecord.get("PATIENT_GIVENNAME");

                // Patient Gender - Values will be "M" or "F"
                String patientGender = nextRecord.get("PATIENT_GENDER");

                // Create the Patient resource
                Patient patient = new Patient();
                patient.setId("Patient/" + patientId);
                patient.addName().setFamily(patientFamilyName).addGiven(patientGivenName);

                // Gender code needs to be mapped
                switch (patientGender) {
                    case "M":
                        patient.setGender(Enumerations.AdministrativeGender.MALE);
                        break;
                    case "F":
                        patient.setGender(Enumerations.AdministrativeGender.FEMALE);
                        break;
                }

                // Upload the patient resource using a client-assigned ID create
                client.update().resource(patient).execute();

                // White blood cell count - This corresponds to LOINC code:
                // Code:        6690-2
                // Display:     Leukocytes [#/volume] in Blood by Automated count
                // Unit System: http://unitsofmeasure.org
                // Unit Code:   10*3/uL
                String rbc = nextRecord.get("RBC");

                // Create the RBC Observation
                Observation rbcObservation = new Observation();
                rbcObservation.setId("Observation/rbc-" + seqN);
                rbcObservation.setStatus(Observation.ObservationStatus.FINAL);
                rbcObservation.setEffective(new DateTimeType(timestamp));
                Coding rbcCode = new Coding()
                    .setSystem("http://loinc.org")
                    .setCode("6690-2")
                    .setDisplay("Leukocytes [#/volume] in Blood by Automated count");
                rbcObservation.getCode().addCoding(rbcCode);
                Quantity rbcValue = new SimpleQuantity()
                    .setSystem("http://unitsofmeasure.org")
                    .setUnit("10*3/uL")
                    .setCode("10*3/uL")
                    .setValue(new BigDecimal(rbc));
                rbcObservation.setValue(rbcValue);
                rbcObservation.setSubject(new Reference("Patient/" + patientId));

                // Upload the RBC Observation resource using a client-assigned ID create
                client.update().resource(rbcObservation).execute();

                // White blood cell count - This corresponds to LOINC code:
                // Code:        789-8
                // Display:     Erythrocytes [#/volume] in Blood by Automated count
                // Unit System: http://unitsofmeasure.org
                // Unit Code:   10*6/uL
                String wbc = nextRecord.get("WBC");

                // Create the WBC Observation
                Observation wbcObservation = new Observation();
                wbcObservation.setId("Observation/wbc-" + seqN);
                wbcObservation.setStatus(Observation.ObservationStatus.FINAL);
                wbcObservation.setEffective(new DateTimeType(timestamp));
                Coding wbcCode = new Coding()
                    .setSystem("http://loinc.org")
                    .setCode("789-8")
                    .setDisplay("Erythrocytes [#/volume] in Blood by Automated count");
                wbcObservation.getCode().addCoding(wbcCode);
                Quantity wbcValue = new SimpleQuantity()
                    .setSystem("http://unitsofmeasure.org")
                    .setUnit("10*6/uL")
                    .setCode("10*6/uL")
                    .setValue(new BigDecimal(wbc));
                wbcObservation.setValue(wbcValue);
                wbcObservation.setSubject(new Reference("Patient/" + patientId));

                // Upload the WBC Observation resource using a client-assigned ID create
                client.update().resource(wbcObservation).execute();

                // Hemoglobin
                // Code:        718-7
                // Display:     Hemoglobin [Mass/volume] in Blood
                // Unit System: http://unitsofmeasure.org
                // Unit Code:   g/dL
                String hb = nextRecord.get("HB");

                // Create the HB Observation
                Observation hbObservation = new Observation();
                hbObservation.setId("Observation/hb-" + seqN);
                hbObservation.setStatus(Observation.ObservationStatus.FINAL);
                hbObservation.setEffective(new DateTimeType(timestamp));
                Coding hbCode = new Coding()
                    .setSystem("http://loinc.org")
                    .setCode("718-7")
                    .setDisplay("Hemoglobin [Mass/volume] in Blood");
                hbObservation.getCode().addCoding(hbCode);
                Quantity hbValue = new SimpleQuantity()
                    .setSystem("http://unitsofmeasure.org")
                    .setUnit("g/dL")
                    .setCode("g/dL")
                    .setValue(new BigDecimal(hb));
                hbObservation.setValue(hbValue);
                hbObservation.setSubject(new Reference("Patient/" + patientId));

                // Upload the HB Observation resource using a client-assigned ID create
                client.update().resource(hbObservation).execute();

            }
        }
    }
}