import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Solution {
  // Essa função recebe uma lista de mensagens, por exemplo:
  //
  // [
  //   "72ff1d14-756a-4549-9185-e60e326baf1b,proposal,created,2019-11-11T14:28:01Z,80921e5f-4307-4623-9ddb-5bf826a31dd7,1141424.0,240",
  //   "af745f6d-d5c0-41e9-b04f-ee524befa425,warranty,added,2019-11-11T14:28:01Z,80921e5f-4307-4623-9ddb-5bf826a31dd7,31c1dd83-8fb7-44ff-8cb7-947e604f6293,3245356.0,DF",
  //   "450951ee-a38d-475c-ac21-f22b4566fb29,warranty,added,2019-11-11T14:28:01Z,80921e5f-4307-4623-9ddb-5bf826a31dd7,c8753500-1982-4003-8287-3b46c75d4803,3413113.45,DF",
  //   "66882b68-baa4-47b1-9cc7-7db9c2d8f823,proponent,added,2019-11-11T14:28:01Z,80921e5f-4307-4623-9ddb-5bf826a31dd7,3f52890a-7e9a-4447-a19b-bb5008a09672,Ismael Streich Jr.,42,62615.64,true"
  // ]
  //
  // Complete a função para retornar uma String com os IDs das propostas válidas no seguinte formato (separado por vírgula):
  // "52f0b3f2-f838-4ce2-96ee-9876dd2c0cf6,51a41350-d105-4423-a9cf-5a24ac46ae84,50cedd7f-44fd-4651-a4ec-f55c742e3477"
  public static String processMessages(List<String> messages) {

    String validProposals = "";

    List<String> proposalIds = getProposalIds(messages);

    for (String proposalId : proposalIds) {

      final boolean isValidLoanValue = isValidLoanValue(proposalId, messages);

      final boolean isValidNumberInstallments = isValidNumberInstallments(proposalId, messages);

      final boolean isValidQtdProponent = isValidQtdProponent(proposalId, messages);

      final boolean isValidQtdProponentMain = isValidQtdProponentMain(proposalId, messages);

      final boolean isValidProponentAge = isValidProponentAge(proposalId, messages);

      final boolean isValidQtdWarranty = isValidQtdWarranty(proposalId, messages);

      final boolean isValidSumWarrantyValue = isValidSumWarrantyValue(proposalId, messages);

      final boolean isValidWarrantyProvince = isValidWarrantyProvince(proposalId, messages);

      final boolean isValidProponentMainMonthlyIncome = isValidProponentMainMonthlyIncome(proposalId, messages);

      if (isValidLoanValue && isValidNumberInstallments && isValidQtdProponent && isValidQtdProponentMain
        && isValidProponentAge && isValidQtdWarranty && isValidSumWarrantyValue
        && isValidWarrantyProvince && isValidProponentMainMonthlyIncome) {

        validProposals += proposalId + ",";

      }

    }

    return validProposals.substring(0, validProposals.length() - 1);
  }

  private static boolean isValidSumWarrantyValue(String proposalId, List<String> messages) {

    final BigDecimal proposalLoanValue = getProposalLoanValue(proposalId, messages).multiply(new BigDecimal("2"));

    BigDecimal sumLoanValue = sumLoanValue(proposalId, messages);

    return sumLoanValue.compareTo(proposalLoanValue) >= 0;
  }

  private static BigDecimal sumLoanValue(String proposalId, List<String> messages) {

    BigDecimal sumLoanValue = new BigDecimal("0");

    for (String message : messages) {

      final String[] split = message.split(",");

      if (split[4].equals(proposalId)) {

        if (message.contains("warranty,added") && !split[7].contains("PR") && !split[7].contains("SC") && !split[7].contains("RS")) {
          sumLoanValue = sumLoanValue.add(new BigDecimal(split[6]));
        }

      }

    }
    return sumLoanValue;
  }

  private static BigDecimal getProposalLoanValue(String proposalId, List<String> messages) {

    for (String message : messages) {

      final String[] split = message.split(",");

      if (split[4].equals(proposalId)) {

        if (isProposalCreated(split)) {

          return new BigDecimal(split[5]);

        }

      }
    }

    return null;

  }

  private static Integer getNumberOfMonthlyInstallments(String proposalId, List<String> messages) {

    for (String message : messages) {

      final String[] split = message.split(",");

      if (split[4].equals(proposalId)) {

        if (isProposalCreated(split)) {

          return Integer.valueOf(split[6]);

        }

      }
    }

    return null;

  }

  private static boolean isValidWarrantyProvince(String proposalId, List<String> messages) {

    for (String message : messages) {

      final String[] split = message.split(",");

      if (split[4].equals(proposalId)) {

        if (message.contains("warranty,added")) {

          if (split[7].contains("PR") || split[7].contains("SC") || split[7].contains("RS")) {

            return false;
          }
        }

      }

    }

    return true;
  }

  private static boolean isValidQtdWarranty(String proposalId, List<String> messages) {

    int count = 0;

    for (String message : messages) {

      final String[] split = message.split(",");

      if (split[4].equals(proposalId)) {

        if (message.contains("warranty,added") && !split[7].contains("PR") && !split[7].contains("SC") && !split[7].contains("RS")) {
          count++;
        }

        if (message.contains("warranty,removed")) {
          count--;
        }

      }

    }

    return count >= 1;
  }

  private static boolean isValidProponentAge(String proposalId, List<String> messages) {

    for (String message : messages) {

      final String[] split = message.split(",");

      if (split[4].equals(proposalId)) {

        if (message.contains("proponent,added")) {

          Integer age = Integer.valueOf(split[7]);

          if (age < 18) {
            return false;
          }

        }

      }

    }

    return true;
  }

  private static boolean isValidQtdProponentMain(String proposalId, List<String> messages) {

    int count = 0;

    for (String message : messages) {

      final String[] split = message.split(",");

      if (split[4].equals(proposalId)) {

        if (message.contains("proponent,added")) {
          if (split[9].equals("true")) {
            count++;
          }
        }

      }

    }

    return count == 1;
  }

  private static boolean isValidProponentMainMonthlyIncome(String proposalId, List<String> messages) {

    for (String message : messages) {

      final String[] split = message.split(",");

      if (split[4].equals(proposalId)) {

        if (message.contains("proponent,added")) {

          if (split[9].equals("true")) {

            Integer age = Integer.valueOf(split[7]);

            final BigDecimal monthlyIncome = new BigDecimal(split[8]);

            final BigDecimal proposalLoanValue = getProposalLoanValue(proposalId, messages);

            final BigDecimal numberOfMonthlyInstallments = new BigDecimal(String.valueOf(getNumberOfMonthlyInstallments(proposalId, messages)));

            final BigDecimal monthlyValue = proposalLoanValue.divideToIntegralValue(numberOfMonthlyInstallments);

            if (age >= 18 && age <= 24) {

              final BigDecimal income = monthlyValue.multiply(new BigDecimal("4"));

              if (monthlyIncome.compareTo(income) == 1) {
                return true;
              }

            } else if (age >= 24 && age <= 50) {

              final BigDecimal income = monthlyValue.multiply(new BigDecimal("3"));

              if (monthlyIncome.compareTo(income) == 1) {
                return true;
              }

            } else if (age > 50) {

              final BigDecimal income = monthlyValue.multiply(new BigDecimal("2"));

              if (monthlyIncome.compareTo(income) == 1) {
                return true;
              }

            }

          }

        }

      }

    }

    return false;
  }

  private static boolean isValidQtdProponent(String proposalId, List<String> messages) {

    int count = 0;

    for (String message : messages) {

      final String[] split = message.split(",");

      if (split[4].equals(proposalId)) {

        if (message.contains("proponent,added")) {
          count++;
        }

      }

    }

    return count >= 2;
  }

  private static boolean isValidNumberInstallments(String proposalId, List<String> messages) {

    for (String message : messages) {

      final String[] split = message.split(",");

      if (split[4].equals(proposalId)) {

        if (isProposalCreated(split)) {

          boolean isValidNumberInstallments = true;

          Integer numberInstallments = Integer.valueOf(split[6]);

          if (numberInstallments < 24 || numberInstallments > 180) {
            isValidNumberInstallments = false;
          }
          return isValidNumberInstallments;

        }

      }

    }

    return false;

  }

  private static boolean isValidLoanValue(String proposalId, List<String> messages) {

    for (String message : messages) {

      final String[] split = message.split(",");

      if (split[4].equals(proposalId)) {

        if (isProposalCreated(split)) {

          boolean isValidLoanValue = true;

          BigDecimal loanValue = new BigDecimal(split[5]);

          BigDecimal minValue = new BigDecimal("30000.0");

          BigDecimal maxValue = new BigDecimal("3000000.0");

          if (loanValue.compareTo(minValue) == -1) {
            isValidLoanValue = false;
          }

          if (loanValue.compareTo(maxValue) == 1) {
            isValidLoanValue = false;
          }

          return isValidLoanValue;

        }

      }

    }

    return false;

  }

  private static boolean isProposalCreated(String[] split) {

    return split[1].equals("proposal") && split[2].equals("created");
  }

  private static List<String> getProposalIds(List<String> messages) {

    List<String> proposalIds = new ArrayList<>();

    messages.forEach(message -> {

      if (message.contains("proposal,created")) {

        final String[] split = message.split(",");

        String proposalId = split[4];

        proposalIds.add(proposalId);

      }

    });
    return proposalIds;
  }
}
