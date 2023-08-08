import java.io.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Lab13_FileListMaker {
    private static ArrayList<String> itemList = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static boolean needsToBeSaved = false;
    private static String currentFileName = null;

    public static void main(String[] args) {
        runMenu();
    }

    private static void runMenu() {
        boolean quit = false;

        while (!quit) {
            displayMenu();
            char choice = SafeInput.getRegExString(scanner, "Enter your choice (A/D/V/O/S/C/Q):", "[AaDdVvOoSsCcQq]").charAt(0);

            switch (choice) {
                case 'A':
                case 'a':
                    addItem();
                    break;
                case 'D':
                case 'd':
                    deleteItem();
                    break;
                case 'V':
                case 'v':
                    viewList();
                    break;
                case 'O':
                case 'o':
                    openList();
                    break;
                case 'S':
                case 's':
                    saveList();
                    break;
                case 'C':
                case 'c':
                    clearList();
                    break;
                case 'Q':
                case 'q':
                    quit = confirmQuit();
                    break;
            }
        }

        System.out.println("Goodbye!");
    }

    private static void displayMenu() {
        SafeInput.prettyHeader("File List Maker Menu");
        System.out.println("A - Add an item to the list");
        System.out.println("D - Delete an item from the list");
        System.out.println("V - View the list");
        System.out.println("O - Open a list from disk");
        System.out.println("S - Save the list to disk");
        System.out.println("C - Clear the list");
        System.out.println("Q - Quit");
    }

    private static void addItem() {
        String item = SafeInput.getNonZeroLenString(scanner, "Enter the item to add:");
        itemList.add(item);
        needsToBeSaved = true;
    }

    private static void deleteItem() {
        if (itemList.isEmpty()) {
            System.out.println("The list is empty.");
            return;
        }

        viewList();

        int index = SafeInput.getRangedInt(scanner, "Enter the number of the item to delete:", 1, itemList.size()) - 1;
        String deletedItem = itemList.remove(index);
        System.out.println("'" + deletedItem + "' has been removed from the list.");
        needsToBeSaved = true;
    }

    private static void viewList() {
        if (itemList.isEmpty()) {
            System.out.println("The list is empty.");
            return;
        }

        System.out.println("Current List:");
        printNumberedItems();
    }

    private static void printNumberedItems() {
        for (int i = 0; i < itemList.size(); i++) {
            System.out.println((i + 1) + ". " + itemList.get(i));
        }
    }

    private static void openList() {
        if (needsToBeSaved) {
            boolean saveBeforeOpen = SafeInput.getYNConfirm(scanner, "You have unsaved changes. Do you want to save before opening a new list?");
            if (saveBeforeOpen) {
                saveList();
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                Scanner fileScanner = new Scanner(selectedFile);
                itemList.clear();
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    itemList.add(line);
                }
                fileScanner.close();
                needsToBeSaved = false;
                currentFileName = selectedFile.getName();
                System.out.println("List loaded from '" + selectedFile.getName() + "'.");
            } catch (FileNotFoundException e) {
                System.out.println("Error loading the file: " + e.getMessage());
            }
        }
    }

    private static void saveList() {
        if (currentFileName == null) {
            currentFileName = SafeInput.getNonZeroLenString(scanner, "Enter the base name for the list (without extension):");
            currentFileName += ".txt";
        }

        try {
            PrintWriter writer = new PrintWriter(currentFileName);
            for (String item : itemList) {
                writer.println(item);
            }
            writer.close();
            needsToBeSaved = false;
            System.out.println("List saved to '" + currentFileName + "'.");
        } catch (IOException e) {
            System.out.println("Error saving the file: " + e.getMessage());
        }
    }

    private static void clearList() {
        if (!itemList.isEmpty()) {
            boolean confirmClear = SafeInput.getYNConfirm(scanner, "Are you sure you want to clear the list?");
            if (confirmClear) {
                itemList.clear();
                System.out.println("List cleared.");
                needsToBeSaved = true;
            }
        } else {
            System.out.println("The list is already empty.");
        }
    }

    private static boolean confirmQuit() {
        if (needsToBeSaved) {
            boolean saveBeforeQuit = SafeInput.getYNConfirm(scanner, "You have unsaved changes. Do you want to save before quitting?");
            if (saveBeforeQuit) {
                saveList();
            }
        }
        return true;
    }
}
