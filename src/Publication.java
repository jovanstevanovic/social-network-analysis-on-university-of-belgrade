import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class Publication {

    // One entry in publication's hash map.
    static class EntryPublication {
        int id;
        String authorsNames;
        int count;
        String publicationYear;
    }

    // Column index enum class for non-filtered publications.
    private enum PublicationNonFilteredColumnIndex {
        PUBLICATION_EMPLOYEE(0), PUBLICATION_TITLE(1), PUBLICATION_YEAR(2), PUBLICATION_AUTHORS(3),
        PUBLICATION_NUM_CITE(4), PUBLICATION_TYPE(5), PUBLICATION_FAC_1(6), PUBLICATION_FAC_2(7),
        PUBLICATION_FAC_3(8), PUBLICATION_NAME(9);

        private int value;

        PublicationNonFilteredColumnIndex(int value) {
            this.value = value;
        }
    }

    // Column index enum class for filtered publications.
    private enum PublicationFilteredColumnIndex {
        PUBLICATION_YEAR(0),  PUBLICATION_AUTHORS(1), PUBLICATION_TYPE(2), PUBLICATION_NAME(3);

        private int value;

        PublicationFilteredColumnIndex(int value) {
            this.value = value;
        }
    }

    enum PublicationFilterType {
        ALL(0), CONFERENCE(1), NON_CONFERENCE(2);

        private int value;

        PublicationFilterType(int value) {
            this.value = value;
        }
    }

    // Fields.
    private static HashMap<String, Author.EntryAuthor> hashMapAuthors;
    private static HashMap<String, Publication.EntryPublication> hashMapPublications;

    // Util methods.
    //// Non-filtered publications.
    private static boolean isPublicationColumnToSkip(int columnIndex) {
        return columnIndex == PublicationNonFilteredColumnIndex.PUBLICATION_EMPLOYEE.value  ||
                columnIndex == PublicationNonFilteredColumnIndex.PUBLICATION_TITLE.value    ||
                columnIndex == PublicationNonFilteredColumnIndex.PUBLICATION_NUM_CITE.value ||
                columnIndex == PublicationNonFilteredColumnIndex.PUBLICATION_FAC_1.value    ||
                columnIndex == PublicationNonFilteredColumnIndex.PUBLICATION_FAC_2.value    ||
                columnIndex == PublicationNonFilteredColumnIndex.PUBLICATION_FAC_3.value    ;
    }

    private static boolean isPublicationRowToSkip(String columnValue) {
        return !(columnValue.equalsIgnoreCase("Article")                ||
                    columnValue.equalsIgnoreCase("Conference Paper")    ||
                    columnValue.equalsIgnoreCase("Article in Press")    ||
                    columnValue.equalsIgnoreCase("Review")              ||
                    columnValue.equalsIgnoreCase("Book Chapter")        ||
                    columnValue.equalsIgnoreCase("Tip rada"))           ;
    }

    //// Filtered publications.
    private static boolean isFilteredPublicationColumnToSkip(int columnIndex) {
        return columnIndex == PublicationFilteredColumnIndex.PUBLICATION_YEAR.value     ||
                columnIndex == PublicationFilteredColumnIndex.PUBLICATION_AUTHORS.value ||
                columnIndex == PublicationFilteredColumnIndex.PUBLICATION_TYPE.value    ;
    }

    private static boolean canPassFilter(String columnValue, PublicationFilterType publicationFilterType) {
        switch (publicationFilterType) {
            case ALL:
                return true;
            case CONFERENCE:
                return columnValue.equalsIgnoreCase("Conference Paper");
            case NON_CONFERENCE:
                return !columnValue.equalsIgnoreCase("Conference Paper");
        }

        return true;
    }

    private static void initializeHashmaps(PublicationFilterType authorFilterType) {

        switch (authorFilterType) {
            case ALL: default:
                hashMapPublications = SharedData.hashMapAllPublications;
                break;
            case CONFERENCE:
                hashMapPublications = SharedData.hashMapConferencePublications;
                break;
            case NON_CONFERENCE:
                hashMapPublications = SharedData.hashMapNonConferencePublications;
                break;
        }

        hashMapAuthors = SharedData.hashMapAllAuthors;
    }

    // Core methods.
    static void firstFilterForPublications(String inputFileName, String outputFileName) throws IOException {
        File authorsFile = new File(inputFileName);
        FileInputStream fis = new FileInputStream(authorsFile);
        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

        File newAuthorsFile = new File(outputFileName);
        FileInputStream newFis = new FileInputStream(newAuthorsFile);
        XSSFWorkbook myNewWorkBook = new XSSFWorkbook(newFis);

        // Processing...
        XSSFSheet myNewSheet = myNewWorkBook.getSheetAt(0);
        int rowNumber = 0;

        for(int i = 0; i < myWorkBook.getNumberOfSheets(); i++) {
            XSSFSheet mySheet = myWorkBook.getSheetAt(i);

            for (Row row : mySheet) {
                int cellNumber = 0;
                Row newRow = myNewSheet.createRow(rowNumber++);
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if(isPublicationColumnToSkip(cell.getColumnIndex())) {
                        continue;
                    }

                    Cell newCell = newRow.createCell(cellNumber++);

                    if(cell.getColumnIndex() == PublicationNonFilteredColumnIndex.PUBLICATION_TYPE.value && isPublicationRowToSkip(cell.getStringCellValue())) {
                        myNewSheet.removeRow(newRow);
                        rowNumber--;
                        break;
                    } else {
                        if(cell.getColumnIndex() == PublicationNonFilteredColumnIndex.PUBLICATION_AUTHORS.value) {
                            newCell.setCellValue(Util.convertNameToEnglishAlphabet(cell.getStringCellValue()));
                        } else {
                            newCell.setCellValue(cell.getStringCellValue());
                        }
                    }
                }
            }
        }

        FileOutputStream os = new FileOutputStream(newAuthorsFile);
        myNewWorkBook.write(os);
        System.out.println("Created filtered file: " + outputFileName + "!");
    }

    static void createPublicationNodeFile(String inputFileName, String outputFileName, PublicationFilterType filterType) throws IOException {
        initializeHashmaps(filterType);

        File authorsFile = new File(inputFileName);
        FileInputStream fis = new FileInputStream(authorsFile);
        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

        File newAuthorsFile = new File(outputFileName);
        FileInputStream newFis = new FileInputStream(newAuthorsFile);
        XSSFWorkbook myNewWorkBook = new XSSFWorkbook(newFis);

        // Processing...
        int id = 0;
        XSSFSheet myNewSheet = myNewWorkBook.getSheetAt(0);
        int rowNumber = 0;
        String publicationYear = "";

        for(int i = 0; i < myWorkBook.getNumberOfSheets(); i++) {
            XSSFSheet mySheet = myWorkBook.getSheetAt(i);

            for (Row row : mySheet) {
                int cellNumber = 0;
                Row newRow = myNewSheet.createRow(rowNumber++);
                Iterator<Cell> cellIterator = row.cellIterator();

                if(rowNumber != 1) {    // If it is header row, then insert Id string instead of Id number.
                    newRow.createCell(cellNumber++).setCellValue(id++);
                } else {
                    newRow.createCell(cellNumber++).setCellValue("Id");
                }

                String authorsNames = "";
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String cellValue = cell.getStringCellValue().toLowerCase();

                    if(cell.getColumnIndex() == PublicationFilteredColumnIndex.PUBLICATION_TYPE.value &&
                            !canPassFilter(cellValue, filterType)) {

                        if(!cellValue.equalsIgnoreCase("Tip rada")) {
                            myNewSheet.removeRow(newRow);
                            rowNumber--;
                            id--;
                            break;
                        }
                    } else {
                        if(!isFilteredPublicationColumnToSkip(cell.getColumnIndex())) {
                            if(!hashMapPublications.containsKey(cellValue)) {
                                Cell newCell = newRow.createCell(cellNumber++);

                                if(!cellValue.equalsIgnoreCase("Ime dokumenta")) {
                                    EntryPublication entry = new EntryPublication();

                                    entry.id = id - 1;
                                    entry.authorsNames = authorsNames;
                                    entry.count = 1;
                                    entry.publicationYear = publicationYear;

                                    hashMapPublications.put(cellValue, entry);
                                } else {
                                    cellValue = "Label";
                                }

                                newCell.setCellValue(cellValue);
                            } else {
                                EntryPublication entry = hashMapPublications.get(cellValue);

                                entry.authorsNames = entry.authorsNames + "-1" + authorsNames;
                                entry.count++;

                                hashMapPublications.put(cellValue, entry);

                                myNewSheet.removeRow(newRow);
                                rowNumber--;
                                id--;
                                break;
                            }
                        } else {
                            if(cell.getColumnIndex() == PublicationFilteredColumnIndex.PUBLICATION_AUTHORS.value) {
                                authorsNames = Util.convertKnownAuthorsNamesToCSVFormat(cellValue);
                            }

                            if(cell.getColumnIndex() == PublicationFilteredColumnIndex.PUBLICATION_YEAR.value) {
                                publicationYear = cellValue;
                            }
                        }
                    }
                }
            }
        }

        FileOutputStream os = new FileOutputStream(newAuthorsFile);
        myNewWorkBook.write(os);
        System.out.println("Created node file: " + outputFileName + "!");
    }

    static void createPublicationEdgeFile(String outputFileName, PublicationFilterType filterType) throws IOException {
        initializeHashmaps(filterType);

        File newAuthorsFile = new File(outputFileName);
        FileInputStream newFis = new FileInputStream(newAuthorsFile);
        XSSFWorkbook myNewWorkBook = new XSSFWorkbook(newFis);
        XSSFSheet myNewSheet = myNewWorkBook.getSheetAt(0);

        // Header creating...
        int id = 0;
        int rowNumber = 0;
        int columnNumber = 0;

        Row headerRow = myNewSheet.createRow(rowNumber++);
        Cell newHeaderCell = headerRow.createCell(columnNumber++);
        newHeaderCell.setCellValue("Id");

        newHeaderCell = headerRow.createCell(columnNumber++);
        newHeaderCell.setCellValue("Source");

        newHeaderCell = headerRow.createCell(columnNumber++);
        newHeaderCell.setCellValue("Target");

        newHeaderCell = headerRow.createCell(columnNumber);
        newHeaderCell.setCellValue("Type");

        for (Map.Entry<String, Author.EntryAuthor> entry1 : hashMapAuthors.entrySet()) {
            String authorName = entry1.getKey();

            for (Map.Entry<String, EntryPublication> entry2 : hashMapPublications.entrySet()) {
                EntryPublication entry = entry2.getValue();
                String[] authorsNames = Util.removeDuplicatedAuthorsFromString(entry.authorsNames.replace("-1", "")).split(",");

                for (String authorsName : authorsNames) {
                    if (authorName.equalsIgnoreCase(authorsName)) {
                        for (Map.Entry<String, EntryPublication> entry3 : hashMapPublications.entrySet()) {
                            EntryPublication entry3Value = entry3.getValue();
                            String[] authorsNames3 = Util.removeDuplicatedAuthorsFromString(entry3Value.authorsNames.replace("-1", "")).split(",");

                            for (String anAuthorsNames3 : authorsNames3) {
                                if (authorName.equalsIgnoreCase(anAuthorsNames3) && entry.id < entry3Value.id) {
                                    columnNumber = 0;
                                    Row newRow = myNewSheet.createRow(rowNumber++);

                                    Cell newCell = newRow.createCell(columnNumber++);
                                    newCell.setCellValue(id++);

                                    newCell = newRow.createCell(columnNumber++);
                                    newCell.setCellValue(entry.id);

                                    newCell = newRow.createCell(columnNumber++);
                                    newCell.setCellValue(entry3Value.id);

                                    newCell = newRow.createCell(columnNumber);
                                    newCell.setCellValue("Undirected");
                                }
                            }
                        }
                    }
                }
            }
        }

        FileOutputStream os = new FileOutputStream(newAuthorsFile);
        myNewWorkBook.write(os);
        System.out.println("Created edge file: " + outputFileName + "!");
    }
}
