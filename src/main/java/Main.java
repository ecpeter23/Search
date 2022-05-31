import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException{
        Website web = new Website("https://www.blakeschool.org/");
        // Website web = new Website("https://en.wikipedia.org/wiki/Weems_v._United_States");
        // Website web = new Website("https://thelistwire.usatoday.com/lists/the-10-closest-presidential-elections-in-u-s-history/");
        // Website web = new Website("https://en.wikipedia.org/wiki/Main_Page");

        // METHODS
        web.read();
        web.sort();
        web.stats();
    }
}
