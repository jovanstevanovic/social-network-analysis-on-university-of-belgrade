import java.util.HashMap;
import java.util.Map;

class Util {

    // Conversions.
    static String convertNameToEnglishAlphabet(String name) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < name.length(); i++) {
            char letter = name.charAt(i);
            char beggingLetter = letter;
            switch (letter) {
                case 'Š':
                    letter = 'S';
                    break;
                case 'Đ':
                    letter = 'D';
                    break;
                case 'Č':
                case 'Ć':
                    letter = 'C';
                    break;
                case 'Ž':
                    letter = 'Z';
                    break;
                case 'š':
                    letter = 's';
                    break;
                case 'đ':
                    letter = 'd';
                    break;
                case 'č':
                case 'ć':
                    letter = 'c';
                    break;
                case 'ž':
                    letter = 'z';
                    break;
            }
            stringBuilder.append(letter);
            if(beggingLetter == 'đ' || beggingLetter == 'Đ') {
                stringBuilder.append('j');
            }
        }

        return stringBuilder.toString();
    }

    static String convertKnownAuthorsNamesToCSVFormat(String authors) {
        if(authors.equalsIgnoreCase("Autori")) {
            return "Autori";
        }

        StringBuilder stringBuffer = new StringBuilder();
        String[] authorsFilter = authors.split(" and ");
        for (String s: authorsFilter) {
            String[] finallyFilteredValues = s.split(",");
            String filteredValueString = finallyFilteredValues[0].trim();

            if(finallyFilteredValues.length > 1) {
                filteredValueString = finallyFilteredValues[0].trim() + finallyFilteredValues[1].charAt(1);
            }

            if(SharedData.hashMapAllAuthors.containsKey(filteredValueString.toLowerCase())) {
                stringBuffer.append(filteredValueString.toLowerCase()).append(",");
            }
        }

        return stringBuffer.toString();
    }

    // Removing duplicates.
    static String removeDuplicatedAuthorsFromString(String collection) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] authors = collection.split(",");
        for (int i = 0; i < authors.length; i++) {
            boolean alreadyAdded = false;
            for(int j = 0; j < i; j++) {
                if (authors[i].equalsIgnoreCase(authors[j])) {
                    alreadyAdded = true;
                    break;
                }
            }

            if(!alreadyAdded) {
                stringBuilder.append(authors[i]).append(",");
            }
        }

        return stringBuilder.toString();
    }

    // Finding methods.
    static void publicationWithMostOccurs(HashMap<String, Publication.EntryPublication> hashMapPublications) {
        int maxPublicationsCount = Integer.MIN_VALUE;
        String publicationWithMaxCount = "";
        for (Map.Entry<String, Publication.EntryPublication> entry : hashMapPublications.entrySet()) {
            String publicationName = entry.getKey();
            int count = entry.getValue().count;

            if(count > maxPublicationsCount) {
                maxPublicationsCount = entry.getValue().count;
                publicationWithMaxCount = publicationName;
            }
        }

        System.out.println("Publikacija sa najvise izdatih radova: " + publicationWithMaxCount + ". Broj radova: " + maxPublicationsCount);
    }

    static int authorsWithMostPublications(int exclude1, int exclude2) {
        int maxPublicationsCount = Integer.MIN_VALUE;
        String authorWithMaxCount = "";
        int authorId = -1;

        for (Map.Entry<String, Author.EntryAuthor> entry : SharedData.hashMapAllAuthors.entrySet()) {
            Author.EntryAuthor entryAuthor = entry.getValue();
            String authorsName = entryAuthor.nameSurname;
            int count = entryAuthor.publicationCount;

            if(count > maxPublicationsCount && (exclude1 == -1 && exclude2 == -1 || exclude1 != -1 && exclude2 == -1 && exclude1 != entryAuthor.id
                    || exclude1 != -1 && exclude2 != -1 && exclude1 != entryAuthor.id && exclude2 != entryAuthor.id)) {

                maxPublicationsCount = entryAuthor.publicationCount;
                authorWithMaxCount = authorsName;
                authorId = entryAuthor.id;
            }
        }

        System.out.println("Autor sa najvise izdatih radova: " + authorWithMaxCount + ". Broj radova: " + maxPublicationsCount);

        return authorId;
    }
}
