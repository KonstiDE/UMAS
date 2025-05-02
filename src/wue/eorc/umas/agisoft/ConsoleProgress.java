package wue.eorc.umas.agisoft;

import com.agisoft.metashape.Progress;

public class ConsoleProgress implements Progress {

    @Override
    public void progress(double v) {
        System.out.printf("Progress: %.2f%%%n", v * 100);
    }

    @Override
    public void status(String s) {
        System.out.println("Status: " + s);
    }

    @Override
    public boolean aborted() {
        return false;
    }
}
