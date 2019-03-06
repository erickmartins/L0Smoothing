package uk.ac.warwick.camdu;


import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 */


/**
 *
 * L0Smoothing - Fiji routine to generate stage reproducibility analysis (max. shifts in X/Y) from an image
 *<p>
 * This class implements a Fiji routine that reads image files, detects beads, crops them, tracks them over time
 * (using TrackMate) and retrieves the maximum X/Y shifts for each individual bead. Finally, it
 * saves the results on a spreadsheet, identifying from which files and bead IDs they come, and saves a single slice
 * indicating the chosen beads and individual tiff files for each selected bead.
 *</p>
 * @param <T> I don't think we actually use this
 * @author Erick Martins Ratamero
 * @version 1.0
 */
@SuppressWarnings({"unchecked"})
@Plugin(type = Command.class, menuPath = "Plugins>L0 Smoothing")
public class L0Smoothing<T extends RealType<T>> implements Command {

    @Parameter
    private Dataset currentData;

    @Parameter
    private UIService uiService;

    @Parameter
    private OpService opService;


    /**
     * main routine function - goes over the list of files, process them and writes the output values
     * <p>
     *     Fairly straightforward method: loops over the list of files that is stored on the class-wide srcDir variable,
     *     check which ones are of the correct extension and contain the string "stage" (both requirements are probably
     *     obsolete now that the user can directly choose files), reads the files, calls "processing" to get outputs
     *     and finally write these outputs into a file.
     * </p>
     */
    @Override
    public void run() {


        final Img<T> image = (Img<T>)currentData.getImgPlus();

        //
        // Enter image processing code here ...
        // The following is just a Gauss filtering example
        //
        final double[] sigmas = {1.0, 3.0, 5.0};

        List<RandomAccessibleInterval<T>> results = new ArrayList<>();

        for (double sigma : sigmas) {
            results.add(opService.filter().gauss(image, sigma));
        }

        // display result
        for (RandomAccessibleInterval<T> elem : results) {
            uiService.show(elem);
        }


    }






    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     */
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();

        // ask the user for a file to open
        final File file = ij.ui().chooseFile(null, "open");

        if (file != null) {
            // load the dataset
            final Dataset dataset = ij.scifio().datasetIO().open(file.getPath());

            // show the image
            ij.ui().show(dataset);

            // invoke the plugin
            ij.command().run(L0Smoothing.class, true);
        }
    }
}



