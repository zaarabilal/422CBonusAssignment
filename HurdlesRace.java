import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class HurdlesRace {
    private static final int TRACK_LENGTH = 42;
    private static final int RUNNER_POSITION = 8;
    private static final int HURDLE_WIDTH = 3;
    private static final int INITIAL_HURDLE_POSITION = 34;
    private static final int JUMP_DURATION = 5;
    private static final int RACE_LENGTH = 10;

    private static final BufferedReader INPUT = new BufferedReader(new InputStreamReader(System.in));
    private static final Random RANDOM = new Random();

    private static int score;
    private static int bestScore;
    private static int jumpTurnsLeft;
    private static int hurdlePosition;
    private static int speed;
    private static String statusMessage = "";

    public static void main(String[] args) throws IOException {
        System.out.println("========================================");
        System.out.println("             HURDLE DASH");
        System.out.println("========================================");
        System.out.println("Race down the track and clear 10 hurdles.");
        System.out.println("Type j and press Enter to jump. Press Enter to run.");
        System.out.println("Press q and Enter to quit.");
        System.out.println();
        System.out.print("Press Enter when you are ready...");
        if (INPUT.readLine() == null) {
            return;
        }

        do {
            playRace();
            System.out.println();
            System.out.print("Run again? (y/n): ");
            String answer = INPUT.readLine();
            if (answer == null || !answer.trim().equalsIgnoreCase("y")) {
                break;
            }
        } while (true);

        System.out.println("Thanks for racing!");
    }

    private static void playRace() throws IOException {
        score = 0;
        jumpTurnsLeft = 0;
        hurdlePosition = INITIAL_HURDLE_POSITION;
        speed = 2;
        statusMessage = "Get ready to run. Jump when the hurdle is about 8-10 spaces away.";

        while (score < RACE_LENGTH) {
            clearScreen();
            drawTrack();
            System.out.println("Hurdles cleared: " + score + " / " + RACE_LENGTH
                    + "    Best: " + bestScore);
            if (jumpTurnsLeft > 0) {
                System.out.println("You are in the air! (" + jumpTurnsLeft + " turns left)");
            }
            if (!statusMessage.isEmpty()) {
                System.out.println(statusMessage);
            }
            System.out.print("Action [Enter = run, j = jump, q = quit]: ");

            String action = INPUT.readLine();
            if (action == null || action.trim().equalsIgnoreCase("q")) {
                statusMessage = "You left the race. Hurdles cleared: " + score;
                System.out.println("You left the race. Hurdles cleared: " + score);
                return;
            }

            String command = action.trim();
            if (!command.isEmpty() && !command.equalsIgnoreCase("j")) {
                statusMessage = "Use Enter to run, j to jump, or q to quit.";
                continue;
            }

            if (command.equalsIgnoreCase("j")) {
                if (jumpTurnsLeft == 0) {
                    jumpTurnsLeft = JUMP_DURATION;
                    statusMessage = "Jump! Keep moving until the hurdle is behind you.";
                } else {
                    statusMessage = "You are already jumping; keep moving!";
                }
            }

            advanceRace();
            if (hasCollision()) {
                clearScreen();
                drawTrack();
                System.out.println("CRASH! You hit a hurdle.");
                System.out.println("You cleared " + score + " hurdle" + (score == 1 ? "" : "s") + ".");
                bestScore = Math.max(bestScore, score);
                return;
            }

            if (jumpTurnsLeft > 0) {
                jumpTurnsLeft--;
            }

            if (hurdlePosition + HURDLE_WIDTH <= RUNNER_POSITION - 1) {
                score++;
                bestScore = Math.max(bestScore, score);
                if (score < RACE_LENGTH) {
                    hurdlePosition = TRACK_LENGTH - 3 + RANDOM.nextInt(7);
                    speed = Math.min(2 + score / 3, 5);
                    statusMessage = "Hurdle cleared! Get ready for the next one.";
                }
            }
        }

        clearScreen();
        drawTrack();
        System.out.println("FINISH! You cleared all " + RACE_LENGTH + " hurdles!");
        System.out.println("Personal best: " + bestScore);
    }

    private static void advanceRace() {
        hurdlePosition -= speed;
    }

    private static void clearScreen() {
        System.out.print("\u001B[H\u001B[2J");
        System.out.flush();
    }

    private static boolean hasCollision() {
        boolean hurdleOverlapsRunner = hurdlePosition <= RUNNER_POSITION + 1
                && hurdlePosition + HURDLE_WIDTH > RUNNER_POSITION - 1;
        return hurdleOverlapsRunner && jumpTurnsLeft == 0;
    }

    private static void drawTrack() {
        char[] track = new char[TRACK_LENGTH];
        for (int index = 0; index < track.length; index++) {
            track[index] = '-';
        }

        if (hurdlePosition >= 0 && hurdlePosition < TRACK_LENGTH) {
            for (int index = hurdlePosition;
                    index < hurdlePosition + HURDLE_WIDTH && index < TRACK_LENGTH;
                    index++) {
                if (index >= 0) {
                    track[index] = '|';
                }
            }
        }

        track[RUNNER_POSITION] = jumpTurnsLeft > 0 ? '^' : 'P';
        String lane = new String(track);
        System.out.println();
        System.out.println("+" + "-".repeat(TRACK_LENGTH) + "+");
        System.out.println("|" + lane + "|");
        System.out.println("+" + "-".repeat(TRACK_LENGTH) + "+");
        System.out.println("          P = runner, ^ = jumping, | = hurdle");
        System.out.println("          Hurdle distance: " + Math.max(0, hurdlePosition - RUNNER_POSITION)
                + " spaces");
    }
}