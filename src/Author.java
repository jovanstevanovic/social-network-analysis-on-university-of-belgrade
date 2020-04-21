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

class Author {

    // One entry in author's hash map.
    static class EntryAuthor {
        int id;
        String nameSurname;
        String faculty;
        String department;
        int publicationCount;
    }

    // Column index enum class.
    private enum AuthorColumnIndex {
        AUTHOR_NAME(0), AUTHOR_SURNAME(1), AUTHOR_MIDDLE_NAME(2), AUTHOR_DEPARTMENT(3), AUTHOR_FACULTY(4);

        private int value;

        AuthorColumnIndex(int value) {
            this.value = value;
        }
    }

    enum AuthorFilterType {
        ALL(0), ETF(1), MATF(2), FONSI(3), FONIS(4), FONIT(5);

        private int value;

        AuthorFilterType(int value) {
            this.value = value;
        }
    }

    // Fields.
    private static HashMap<String, EntryAuthor> hashMapAuthors;
    private static HashMap<String, Publication.EntryPublication> hashMapPublications;

    // Util methods.
    private static boolean isNotHeaderAlreadyWritten(int rowNumber, String columnValue) {
        return !(columnValue.equalsIgnoreCase("Ime")        ||
                columnValue.equalsIgnoreCase("Prezime")     ||
                columnValue.equalsIgnoreCase("Odsek")       ||
                columnValue.equalsIgnoreCase("Fakultet"))   ||
                rowNumber <= 1                                          ;
    }

    private static boolean canPassFilter(String faculty, String department, AuthorFilterType authorFilterType) {
        switch (authorFilterType) {
            case ALL:
                return true;
            case ETF:
                return faculty.equalsIgnoreCase("elektrotehnicki fakultet");
            case MATF:
                return faculty.equalsIgnoreCase("matematicki fakultet");
            case FONSI:
                return faculty.equalsIgnoreCase("fakultet organizacionih nauka")            &&
                        department.equalsIgnoreCase("Katedra za softversko inzenjerstvo")   ;
            case FONIS:
                return faculty.equalsIgnoreCase("fakultet organizacionih nauka")            &&
                        department.equalsIgnoreCase("Katedra za informacione sisteme")      ;
            case FONIT:
                return faculty.equalsIgnoreCase("fakultet organizacionih nauka")            &&
                        department.equalsIgnoreCase("Katedra za informacione tehnologije")  ;
        }

        return true;
    }

    private static void initializeHashmaps(AuthorFilterType authorFilterType) {
        switch (authorFilterType) {
            case ALL: default:
                hashMapAuthors = SharedData.hashMapAllAuthors;
                break;
            case ETF:
                hashMapAuthors = SharedData.hashMapETFAuthors;
                break;
            case MATF:
                hashMapAuthors = SharedData.hashMapMATFAuthors;
                break;
            case FONSI:
                hashMapAuthors = SharedData.hashMapFONSIAuthors;
                break;
            case FONIS:
                hashMapAuthors = SharedData.hashMapFONISAuthors;
                break;
            case FONIT:
                hashMapAuthors = SharedData.hashMapFONITAuthors;
                break;
        }

        hashMapPublications = SharedData.hashMapAllPublications;
    }

    // Core methods.
    static void createAuthorNodeFile(String inputFileName, String outputFileName, AuthorFilterType filterType) throws IOException {
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
        String nameString = "";
        String surnameString = "";
        String departmentString = "";

        for(int i = 0; i < myWorkBook.getNumberOfSheets(); i++) {
            XSSFSheet mySheet = myWorkBook.getSheetAt(i);

            for (Row row : mySheet) {
                int cellNumber = 0;
                Row newRow = myNewSheet.createRow(rowNumber++);

                if(rowNumber != 1) {    // If it is header row, then insert Id string instead of Id number.
                    newRow.createCell(cellNumber++).setCellValue(id++);
                } else {
                    newRow.createCell(cellNumber++).setCellValue("Id");
                }

                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if(cell.getColumnIndex() == AuthorColumnIndex.AUTHOR_MIDDLE_NAME.value) {
                        continue;
                    }

                    String cellValue = cell.getStringCellValue();
                    if(isNotHeaderAlreadyWritten(rowNumber, cellValue)) {
                        cellValue = Util.convertNameToEnglishAlphabet(cellValue);

                        if(cell.getColumnIndex() == AuthorColumnIndex.AUTHOR_NAME.value) {
                            nameString = cellValue;
                        } else {
                            if(cell.getColumnIndex() == AuthorColumnIndex.AUTHOR_SURNAME.value) {
                                Cell newCell = newRow.createCell(cellNumber++);
                                surnameString = cellValue;

                                if(cellValue.equalsIgnoreCase("Prezime")) {
                                    newCell.setCellValue("Label");
                                } else {
                                    newCell.setCellValue(nameString + " " + cellValue);
                                }

                            } else {
                                if(cell.getColumnIndex() == AuthorColumnIndex.AUTHOR_DEPARTMENT.value) {
                                    departmentString = cellValue;
                                }

                                if(cell.getColumnIndex() == AuthorColumnIndex.AUTHOR_FACULTY.value) {
                                    if(rowNumber > 1) {
                                        if(canPassFilter(cellValue, departmentString, filterType)) {
                                            EntryAuthor entryAuthor = new EntryAuthor();

                                            entryAuthor.id = id - 1;
                                            entryAuthor.nameSurname = nameString + " " + surnameString;
                                            entryAuthor.department = departmentString;
                                            entryAuthor.faculty = cellValue;
                                            entryAuthor.publicationCount = 0;

                                            hashMapAuthors.put(surnameString.toLowerCase() + nameString.toLowerCase().charAt(0), entryAuthor);
                                        } else {
                                            myNewSheet.removeRow(newRow);
                                            rowNumber--;
                                            id--;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        myNewSheet.removeRow(newRow);
                        rowNumber--;
                        id--;
                        break;
                    }
                }
            }
        }

        FileOutputStream os = new FileOutputStream(newAuthorsFile);
        myNewWorkBook.write(os);
        System.out.println("Created node file: " + outputFileName + "!");
    }

    static void createAuthorEdgeFile(String outputFileName, AuthorFilterType filterType) throws IOException {
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

        for (Map.Entry<String, Publication.EntryPublication> publication : hashMapPublications.entrySet()) {
            Publication.EntryPublication entry = publication.getValue();
            String[] authorsCollectionsNames = entry.authorsNames.split("-1");

            for (String s : authorsCollectionsNames) {
                String[] authorsNames = s.split(",");

                for(String author1Name : authorsNames) {
                    if(!hashMapAuthors.containsKey(author1Name.toLowerCase())) {
                        continue;
                    }

                    EntryAuthor author1 = hashMapAuthors.get(author1Name.toLowerCase());
                    author1.publicationCount++;

                    for (String author2Name : authorsNames) {
                        if(author1Name.equalsIgnoreCase(author2Name) || !hashMapAuthors.containsKey(author2Name.toLowerCase())) {
                            continue;
                        }

                        EntryAuthor author2 = hashMapAuthors.get(author2Name.toLowerCase());
                        if(author1.id < author2.id) {
                            continue;
                        }

                        columnNumber = 0;
                        Row newRow = myNewSheet.createRow(rowNumber++);

                        Cell newCell = newRow.createCell(columnNumber++);
                        newCell.setCellValue(id++);

                        newCell = newRow.createCell(columnNumber++);
                        newCell.setCellValue(author1.id);

                        newCell = newRow.createCell(columnNumber++);
                        newCell.setCellValue(author2.id);

                        newCell = newRow.createCell(columnNumber);
                        newCell.setCellValue("Undirected");
                    }
                }
            }
        }

        FileOutputStream os = new FileOutputStream(newAuthorsFile);
        myNewWorkBook.write(os);
        System.out.println("Created edge file: " + outputFileName + "!");
    }
}
