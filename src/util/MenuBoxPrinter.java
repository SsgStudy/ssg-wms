package util;

public class MenuBoxPrinter { // 문자열의 실제 표시 길이를 계산하는 메소드
    // Method to calculate the display width of a string accounting for double-width Korean characters
    private static int getStringDisplayLength(String str) {
        int length = 0;
        for (char c : str.toCharArray()) {
            if (c >= '가' && c <= '힣') {
                length += 2;  // Korean characters occupy two spaces
            } else {
                length += 1;  // Other characters occupy one space
            }
        }
        return length;
    }

    // Method to print a line with proper length considering the box width
    private static void printLine(int boxWidth) {
        System.out.print("+");
        for (int i = 0; i < boxWidth - 2; i++) {
            System.out.print("-");
        }
        System.out.println("+");
    }

    // Method to print the menu box with title and items
    public static void printMenuBoxWithTitle(String title, String[] menuItems) {
        int boxWidth = 30;

        printLine(boxWidth);  // Top border

        // Title - centered
        int titleLength = getStringDisplayLength(title);
        int paddingBeforeTitle = (boxWidth - 2 - titleLength) / 2;
        System.out.print("|" + " ".repeat(paddingBeforeTitle) + title);
        System.out.println(" ".repeat(boxWidth - 2 - titleLength - paddingBeforeTitle) + "|");

        printLine(boxWidth);  // Separator

        // Menu items - left-aligned
        for (String item : menuItems) {
            int itemLength = getStringDisplayLength(item);
            System.out.print("| " + item);
            System.out.println(" ".repeat(boxWidth - 2 - itemLength) + "|");
        }

        printLine(boxWidth);  // Bottom border

        System.out.println("메뉴를 선택해주세요");  // Prompt for user input
    }

}