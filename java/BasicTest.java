
/**
 * Programming Styles WiSe 20-21 Basic Test Case
 * 
 * @author gambi
 */
//See https://www.baeldung.com/hamcrest-text-matchers
import static org.hamcrest.text.IsBlankString.blankOrNullString;
import static org.hamcrest.core.StringEndsWith.endsWith;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

//See https://junit.org/junit4/javadoc/latest/deprecated-list.html
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * This class contains basic test cases that illustrate how we can test
 * Minesweeper
 * 
 * @author gambi
 *
 */
/*
 * A test without category annotation means that we always run its tests.
 */
public class BasicTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@BeforeClass
	public static void checkPrecondition() {
		/*
		 * Check that the required variables and configurations are set
		 */
		MinesweeperTestUtils.validateTheExecutionEnvironment();
	}

	@Test(timeout = 3000)
	public void testThatGivenCorrectInputsTheProgramExitNormally() throws Exception {
		// Setup: Provide the configuration file
		//
		// Create a temporary file.
		final File boardCfgFile = tempFolder.newFile("simple.cfg");

		// Write the simple 3x3 board to the file
		try (PrintWriter out = new PrintWriter(boardCfgFile)) {
			out.println("..*");
			out.println("...");
			out.println("...");
		}

		// Define the list of inputs to provide to Minesweeper
		List<String> inputSequence = new ArrayList<String>();
		// Input 1: space space 1 space 1 space R
		inputSequence.add("  1 1 R");

		// Execute Minesweeper in a separate process starting it with the boardCfgFile
		// and passing the inputSequence
		Map<String, Object> result = MinesweeperTestUtils.execute(boardCfgFile, inputSequence);

		int exitCode = (Integer) result.get("exitCode");
		String stdOut = (String) result.get("stdOut");
		String stdError = (String) result.get("stdError");

		// Assertions

		// Did the program exit normally?
		Assert.assertEquals(MinesweeperTestUtils.MINESWEEPER_CLASS_NAME + " did not exit normally. Error message: "
				+ stdError + "\n", 0, exitCode);

		// Did program produce any output at all?
		MatcherAssert.assertThat(MinesweeperTestUtils.MINESWEEPER_CLASS_NAME + " did not produced any output!", stdOut,
				Matchers.not(blankOrNullString()));
	}

	@Test(timeout = 3000)
	public void testNoConfigurationPassed() throws Exception {
		Map<String, Object> result = MinesweeperTestUtils.execute(null, Collections.emptyList());
		int exitCode = (Integer) result.get("exitCode");

		Assert.assertEquals(MinesweeperTestUtils.MINESWEEPER_CLASS_NAME + " started without passed configuration", 1,
				exitCode);
	}

	@Test(timeout = 3000)
	public void testConfigurationNotExisting() throws Exception {
		Map<String, Object> result = MinesweeperTestUtils.execute(new File("nope.cfg"), Collections.emptyList());
		int exitCode = (Integer) result.get("exitCode");

		Assert.assertEquals(MinesweeperTestUtils.MINESWEEPER_CLASS_NAME + " started without existing configuration", 1,
				exitCode);
	}

	@Test(timeout = 3000)
	public void testConfigurationIsNotARegularFile() throws Exception {
		File folder = tempFolder.newFolder();
		Map<String, Object> result = MinesweeperTestUtils.execute(folder, Collections.emptyList());
		int exitCode = (Integer) result.get("exitCode");

		Assert.assertEquals(
				MinesweeperTestUtils.MINESWEEPER_CLASS_NAME + " started when given a folder as configuration file", 1,
				exitCode);
	}

	@Test(timeout = 3000)
	public void testInvalidFilenameOnlySuffixMixedCase() throws Exception {
		final File boardCfgFile = tempFolder.newFile(".CfG");
		Map<String, Object> result = MinesweeperTestUtils.execute(boardCfgFile, Collections.emptyList());
		int exitCode = (Integer) result.get("exitCode");
		Assert.assertEquals(MinesweeperTestUtils.MINESWEEPER_CLASS_NAME + " accepted invalid file name '.CfG'", 2,
				exitCode);
	}

	@Test(timeout = 3000)
	public void testInvalidFilenameWrongSuffixFormat() throws Exception {
		final File boardCfgFile = tempFolder.newFile("test.ccfg");
		Map<String, Object> result = MinesweeperTestUtils.execute(boardCfgFile, Collections.emptyList());
		int exitCode = (Integer) result.get("exitCode");
		Assert.assertEquals(MinesweeperTestUtils.MINESWEEPER_CLASS_NAME + " accepted invalid file name 'test.ccfg'", 2,
				exitCode);
	}

	@Test(timeout = 3000)
	public void testInvalidFilenameWrongSuffix() throws Exception {
		final File boardCfgFile = tempFolder.newFile("test.foo");
		Map<String, Object> result = MinesweeperTestUtils.execute(boardCfgFile, Collections.emptyList());
		int exitCode = (Integer) result.get("exitCode");
		Assert.assertEquals(MinesweeperTestUtils.MINESWEEPER_CLASS_NAME + " accepted invalid file name 'test.foo'", 2,
				exitCode);
	}

	@Test(timeout = 3000)
	public void testEmptyConfiguration() throws Exception {
		final File boardCfgFile = tempFolder.newFile("simple.cfg");

		try (PrintWriter out = new PrintWriter(boardCfgFile)) {
			out.println("");
		}

		Map<String, Object> result = MinesweeperTestUtils.execute(boardCfgFile, Collections.emptyList());
		int exitCode = (Integer) result.get("exitCode");

		Assert.assertEquals(MinesweeperTestUtils.MINESWEEPER_CLASS_NAME + " accepted empty configuration", 2, exitCode);
	}

	@Test(timeout = 3000)
	public void testInvalidContentInConfiguration() throws Exception {
		final File boardCfgFile = tempFolder.newFile("simple.cfg");

		try (PrintWriter out = new PrintWriter(boardCfgFile)) {
			out.println("a");
		}

		Map<String, Object> result = MinesweeperTestUtils.execute(boardCfgFile, Collections.emptyList());
		int exitCode = (Integer) result.get("exitCode");

		Assert.assertEquals(
				MinesweeperTestUtils.MINESWEEPER_CLASS_NAME + " accepted configuration with invalid content", 2,
				exitCode);
	}

	@Test(timeout = 3000)
	public void testNonRectangularConfiguration() throws Exception {
		final File boardCfgFile = tempFolder.newFile("simple.cfg");

		try (PrintWriter out = new PrintWriter(boardCfgFile)) {
			out.println("...");
			out.println("*");
		}

		Map<String, Object> result = MinesweeperTestUtils.execute(boardCfgFile, Collections.emptyList());
		int exitCode = (Integer) result.get("exitCode");

		Assert.assertEquals(
				MinesweeperTestUtils.MINESWEEPER_CLASS_NAME + " accepted configuration that is not rectangular", 2,
				exitCode);
	}
}
