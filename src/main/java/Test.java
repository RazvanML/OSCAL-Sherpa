import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.tarponsoftware.oscal.InlineMarkupType;
import com.tarponsoftware.oscal.OscalCatalogCatalogASSEMBLY;
import com.tarponsoftware.oscal.OscalCatalogControlASSEMBLY;
import com.tarponsoftware.oscal.OscalCatalogGroupASSEMBLY;
import com.tarponsoftware.oscal.OscalControlCommonPartASSEMBLY;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class Test {


    public static List<Serializable> getContent(List<InlineMarkupType> in) {
        List<Serializable> ret = new ArrayList<>();
        for (InlineMarkupType o: in)
            ret.addAll(o.getContent());
        return ret;
    }

    public static <T> List<T> getValues(List<JAXBElement<?> > in) {
        List<T> ret = new ArrayList<>();
        for (JAXBElement<?> o: in)
            ret.add((T)o.getValue());
        return ret;
    }


    public static void listParts(String head, List<OscalControlCommonPartASSEMBLY> parts) {
        if (parts.size() == 0)
            return;
        for (OscalControlCommonPartASSEMBLY p : parts) {
            String title = "Null";
            title = p.getId() + getContent(getValues(p.getBlockElementGroup()));
            System.out.println(head + p.getId() + title);
            listParts(head + " ", p.getPart());
        }

    }

    public static void main(String[] args) throws JAXBException, FileNotFoundException {

        JAXBContext jaxbContext = JAXBContext.newInstance("com.tarponsoftware.oscal");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        // Assuming the root element in the OSCAL file contains a list of tasks
        // Adjust according to your OSCAL schema structure
         FileInputStream inputStream = new FileInputStream("/home/razvan/Downloads/NIST_SP-800-53_rev5_catalog.xml");
        // FileInputStream inputStream = new FileInputStream("/home/razvan/Downloads/NIST_SP-800-171_rev2_catalog.xml");

        Source src = new StreamSource(inputStream);

        JAXBElement<OscalCatalogCatalogASSEMBLY> obj = unmarshaller.unmarshal(src, OscalCatalogCatalogASSEMBLY.class);
        OscalCatalogCatalogASSEMBLY oscalRoot = obj.getValue();
        for (OscalCatalogGroupASSEMBLY g : oscalRoot.getGroup()) {
            System.out.println("Group: " + g.getId() + " " + g.getTitle().getContent());
            listParts("", g.getPart());
            for (OscalCatalogControlASSEMBLY c : g.getControl()) {
                System.out.println("Countrol: " + c.getId() + " " + c.getTitle().getContent());
                listParts("", c.getPart());
            }
        }
    }
}
