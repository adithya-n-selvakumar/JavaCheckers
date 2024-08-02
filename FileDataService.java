package org.cis1200;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileDataService {

    public static boolean loadCurrentGameInProgressData() {
        boolean currentGameInProgressData = false;
        try {
            File file = new File(
                    EnvironmentConstants.SNAPSHOT_DIR + "/boardCurrentGameInProgressData.txt"
            );

            if (file.exists()) {
                InputStream inputStream = new FileInputStream(file);
                String boardCurrentGameInProgressData = readFromInputStream(inputStream);
                currentGameInProgressData = Boolean.parseBoolean(boardCurrentGameInProgressData);
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return currentGameInProgressData;
    }

    public static int loadCurrentPlayerData() {
        int currentPlayerData = 0;
        try {
            File file = new File(EnvironmentConstants.SNAPSHOT_DIR + "/boardCurrentPlayerData.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            InputStream inputStream = new FileInputStream(file);
            String boardCurrentPlayerDataStr = readFromInputStream(inputStream);
            currentPlayerData = Integer.parseInt(boardCurrentPlayerDataStr);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return currentPlayerData;
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (resultStringBuilder != null) {
                    resultStringBuilder.append("\n").append(line);
                } else {
                    resultStringBuilder = new StringBuilder();
                    resultStringBuilder.append(line);
                }
            }
        }
        return resultStringBuilder.toString();
    }

    public static boolean removeCurrentGameInProgressData() {
        boolean isSuccessfulDelete = false;
        try {
            File file = new File(
                    EnvironmentConstants.SNAPSHOT_DIR + "/boardCurrentGameInProgressData.txt"
            );

            if (file.exists()) {
                isSuccessfulDelete = file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            isSuccessfulDelete = false;
        }

        return isSuccessfulDelete;
    }

    public static boolean removeCurrentPlayerData() {
        boolean isSuccessfulDelete = false;
        try {
            File file = new File(EnvironmentConstants.SNAPSHOT_DIR + "/boardCurrentPlayerData.txt");

            if (file.exists()) {
                isSuccessfulDelete = file.delete();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            isSuccessfulDelete = false;
        }

        return isSuccessfulDelete;
    }

    public static Boolean writeCurrentGameInProgressData(boolean currentGameInProgressData) {
        Boolean isSuccessfulWrite = false;
        try {
            File file = new File(
                    EnvironmentConstants.SNAPSHOT_DIR + "/boardCurrentGameInProgressData.txt"
            );

            if (!file.exists()) {
                file.createNewFile();
            }
            String boardCurrentGameInProgressDataStr = String.valueOf(currentGameInProgressData);

            BufferedWriter fWriter = new BufferedWriter(
                    new FileWriter(file)
            );
            fWriter.write(boardCurrentGameInProgressDataStr);
            fWriter.close();
            isSuccessfulWrite = true;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return isSuccessfulWrite;
    }

    public static Boolean writeCurrentPlayerData(int currentPlayerData) {
        Boolean isSuccessfulWrite = false;
        try {
            File file = new File(EnvironmentConstants.SNAPSHOT_DIR + "/boardCurrentPlayerData.txt");

            if (file.exists()) {
                file.createNewFile();
            }
            String boardCurrentPlayerDataStr = String.valueOf(currentPlayerData);

            BufferedWriter fWriter = new BufferedWriter(
                    new FileWriter(file)
            );
            fWriter.write(boardCurrentPlayerDataStr);
            fWriter.close();
            isSuccessfulWrite = true;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return isSuccessfulWrite;
    }

    public static int[][] readPreviousStatefromFile() {
        int[][] checkersPosition = null;
        int cols = 0;
        List<Integer[]> listOfIntegerArray = new ArrayList<Integer[]>();

        try {
            File file = new File(EnvironmentConstants.SNAPSHOT_DIR + "/boardCurrentStatus.txt");

            if (!file.exists()) {
                return null;
            }

            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String[] strArrayLine = (String[]) sc.nextLine().trim().split(" ");
                cols = strArrayLine.length;

                Integer[] bigIntegerArrayLine = new Integer[strArrayLine.length];
                for (int j = 0; j < strArrayLine.length; j++) {
                    bigIntegerArrayLine[j] = Integer.parseInt(strArrayLine[j]);
                }
                listOfIntegerArray.add(bigIntegerArrayLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        int rows = listOfIntegerArray.size();
        checkersPosition = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            Integer[] integerArray = listOfIntegerArray.get(i);
            for (int j = 0; j < cols; j++) {
                checkersPosition[i][j] = integerArray[j];
            }
        }

        return checkersPosition;
    }

    public static boolean removeCurrentStatusData() {
        boolean isSuccessfulDelete = false;
        try {
            File file = new File(EnvironmentConstants.SNAPSHOT_DIR + "/boardCurrentStatus.txt");

            if (file.exists()) {
                isSuccessfulDelete = file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            isSuccessfulDelete = false;
        }

        return isSuccessfulDelete;
    }

    public static boolean writeCurrentStateToFile(int[][] checkersPosition) {
        try {
            File file = new File(EnvironmentConstants.SNAPSHOT_DIR + "/boardCurrentStatus.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter fWriter = new BufferedWriter(new FileWriter(file));

            for (int i = 0; i < checkersPosition.length; i++) {
                StringBuffer strBuffer = new StringBuffer();
                for (int j = 0; j < checkersPosition[i].length; j++) {
                    if (j == (checkersPosition[i].length - 1)) {
                        strBuffer.append(checkersPosition[i][j]);
                    } else {
                        strBuffer.append(checkersPosition[i][j] + " ");
                    }
                }
                fWriter.write(strBuffer.toString());
                if (i < (checkersPosition.length - 1)) {
                    fWriter.newLine();
                }
            }
            fWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
