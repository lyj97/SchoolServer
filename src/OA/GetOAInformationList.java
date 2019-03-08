package OA;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetOAInformationList {

    public static void getList(){
        try {
            Document doc = Jsoup.connect("https://oa.jlu.edu.cn/defaultroot/PortalInformation!jldxList.action?channelId=179577").get();
            Elements links = doc.select("a.font14[href]");
//            Elements media = doc.select("[src]");
//            Elements imports = doc.select("link[href]");

//            print("\nMedia: (%d)", media.size());
//            for (Element src : media) {
//                if (src.tagName().equals("img"))
//                    print(" * %s: <%s> %sx%s (%s)",
//                            src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
//                            trim(src.attr("alt"), 20));
//                else
//                    print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
//            }
//
//            print("\nImports: (%d)", imports.size());
//            for (Element link : imports) {
//                print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
//            }

            print("\nLinks: (%d)", links.size());
            for (Element link : links) {
                link.select("font").remove();
                print(" * a: \n%s\n%s\n", link.attr("abs:href"), trim(link.text(), 38));

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-3) + "...";
        else
            return s;
    }

    public static void main(String args[]){
        getList();
    }

}
