import java.io.IOException;

public class Main {

    private static String[][] fileNames = {
            {"PrimaryDataset/UB_cs_authors.xlsx", "PrimaryDataset/UB_CS_papers_scopus.xlsx"},
            {"SecondaryDataset/Authors/AuthorsAllNodeFile.xlsx", "SecondaryDataset/Authors/AuthorsAllEdgeFile.xlsx"},
            {"SecondaryDataset/Authors/AuthorsETFNodeFile.xlsx", "SecondaryDataset/Authors/AuthorsETFEdgeFile.xlsx"},
            {"SecondaryDataset/Authors/AuthorsMATFNodeFile.xlsx", "SecondaryDataset/Authors/AuthorsMATFEdgeFile.xlsx"},
            {"SecondaryDataset/Authors/AuthorsFONSINodeFile.xlsx", "SecondaryDataset/Authors/AuthorsFONSIEdgeFile.xlsx"},
            {"SecondaryDataset/Authors/AuthorsFONITNodeFile.xlsx", "SecondaryDataset/Authors/AuthorsFONITEdgeFile.xlsx"},
            {"SecondaryDataset/Authors/AuthorsFONISNodeFile.xlsx", "SecondaryDataset/Authors/AuthorsFONISEdgeFile.xlsx"},
            {"SecondaryDataset/Publications/PublicationAllNodeFile.xlsx", "SecondaryDataset/Publications/PublicationAllEdgeFile.xlsx"},
            {"SecondaryDataset/Publications/PublicationConferenceNodeFile.xlsx", "SecondaryDataset/Publications/PublicationConferenceEdgeFile.xlsx"},
            {"SecondaryDataset/Publications/PublicationNonConferenceNodeFile.xlsx", "SecondaryDataset/Publications/PublicationNonConferenceEdgeFile.xlsx"},
            {"SecondaryDataset/FilteredPrimaryDataset/UB_CS_papers_scopus_filtered.xlsx"}
    };

    // Initializations.
    //// Author initializations.
    private static void initializeAuthorNodeFiles() throws IOException {
        Author.createAuthorNodeFile(fileNames[0][0], fileNames[1][0], Author.AuthorFilterType.ALL);
        Author.createAuthorNodeFile(fileNames[0][0], fileNames[2][0], Author.AuthorFilterType.ETF);
        Author.createAuthorNodeFile(fileNames[0][0], fileNames[3][0], Author.AuthorFilterType.MATF);
        Author.createAuthorNodeFile(fileNames[0][0], fileNames[4][0], Author.AuthorFilterType.FONSI);
        Author.createAuthorNodeFile(fileNames[0][0], fileNames[5][0], Author.AuthorFilterType.FONIT);
        Author.createAuthorNodeFile(fileNames[0][0], fileNames[6][0], Author.AuthorFilterType.FONIS);
    }

    private static void initializeAuthorEdgesFiles() throws IOException {
        Author.createAuthorEdgeFile(fileNames[1][1], Author.AuthorFilterType.ALL);
        Author.createAuthorEdgeFile(fileNames[2][1], Author.AuthorFilterType.ETF);
        Author.createAuthorEdgeFile(fileNames[3][1], Author.AuthorFilterType.MATF);
        Author.createAuthorEdgeFile(fileNames[4][1], Author.AuthorFilterType.FONSI);
        Author.createAuthorEdgeFile(fileNames[5][1], Author.AuthorFilterType.FONIT);
        Author.createAuthorEdgeFile(fileNames[6][1], Author.AuthorFilterType.FONIS);
    }

    //// Publication initializations.
    private static void initializePublicationNodeFiles() throws IOException {
        Publication.firstFilterForPublications(fileNames[0][1], fileNames[10][0]);

        Publication.createPublicationNodeFile(fileNames[10][0], fileNames[7][0], Publication.PublicationFilterType.ALL);
        Publication.createPublicationNodeFile(fileNames[10][0], fileNames[8][0], Publication.PublicationFilterType.CONFERENCE);
        Publication.createPublicationNodeFile(fileNames[10][0], fileNames[9][0], Publication.PublicationFilterType.NON_CONFERENCE);
    }

    private static void initializePublicationEdgeFiles() throws IOException {
        Publication.createPublicationEdgeFile(fileNames[7][1], Publication.PublicationFilterType.ALL);
        Publication.createPublicationEdgeFile(fileNames[8][1], Publication.PublicationFilterType.CONFERENCE);
        Publication.createPublicationEdgeFile(fileNames[9][1], Publication.PublicationFilterType.NON_CONFERENCE);
    }

    private static void printResultAllUtilMethods() {
        // Authors with most occurs in general, in conference publications and in non-conference publications.
        int id1 = Util.authorsWithMostPublications(-1, -1);     // First in number of occurs.
        int id2 = Util.authorsWithMostPublications(id1, -1);    // Second in number of occurs.
        Util.authorsWithMostPublications(id1, id2);             // Third in number of occurs.

        // Conference publications with most occurs.
        Util.publicationWithMostOccurs(SharedData.hashMapConferencePublications);

        // Non-conference publication with most occurs.
        Util.publicationWithMostOccurs(SharedData.hashMapNonConferencePublications);
    }

    public static void main(String[] args) {
        try {
            // Node initialization.
            initializeAuthorNodeFiles();
            initializePublicationNodeFiles();

            // Edge initialization.
            initializeAuthorEdgesFiles();
            initializePublicationEdgeFiles();

            // Printing result of all util methods.
            printResultAllUtilMethods();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
