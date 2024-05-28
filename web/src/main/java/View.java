
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.tarponsoftware.oscal.InlineMarkupType;
import com.tarponsoftware.oscal.OscalCatalogCatalogASSEMBLY;
import com.tarponsoftware.oscal.OscalCatalogControlASSEMBLY;
import com.tarponsoftware.oscal.OscalCatalogGroupASSEMBLY;
import com.tarponsoftware.oscal.OscalControlCommonPartASSEMBLY;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

class TreeItem {
    String value;

    List<TreeItem> child;

    public List<TreeItem> getChild() {
        return child;
    }

    public TreeItem(String value, TreeItem... children) {
        this.value = value;
        child = new ArrayList<>();
        child.addAll(Arrays.asList(children));
    }
}

@Route("")
public class View extends VerticalLayout {

    public  List<Serializable> getContent(List<InlineMarkupType> in) {
        List<Serializable> ret = new ArrayList<>();
        for (InlineMarkupType o: in)
        // TODO  - some of them are String, some ar JAXBElement<InsertType> or other
            ret.addAll(o.getContent());
        return ret;
    }

    public  <T> List<T> getValues(List<JAXBElement<?> > in) {
        List<T> ret = new ArrayList<>();
        for (JAXBElement<?> o: in)
            ret.add((T)o.getValue());
        return ret;
    }

    
    public  void listParts(TreeItem parent, List<OscalControlCommonPartASSEMBLY> parts) {
        for (OscalControlCommonPartASSEMBLY p : parts) {
            TreeItem ti = new TreeItem("<I>Part:</I><B>" + p.getId() +"</B>" + getContent(getValues(p.getBlockElementGroup())));
            listParts(ti, p.getPart());
            parent.child.add(ti);
        }
    }

    public View() throws JAXBException, FileNotFoundException {
        getStyle().setHeight("100%");
        TreeGrid<TreeItem> treeGrid = new TreeGrid<>();
        treeGrid.getStyle().set("height", "100%");


        List<TreeItem> data = new ArrayList<>();


        JAXBContext jaxbContext = JAXBContext.newInstance("com.tarponsoftware.oscal");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        // Assuming the root element in the OSCAL file contains a list of tasks
        // Adjust according to your OSCAL schema structure
         FileInputStream inputStream = new FileInputStream("data/NIST_SP-800-53_rev5_catalog.xml");
        // FileInputStream inputStream = new FileInputStream("/home/razvan/Downloads/NIST_SP-800-171_rev2_catalog.xml");

        Source src = new StreamSource(inputStream);

        JAXBElement<OscalCatalogCatalogASSEMBLY> obj = unmarshaller.unmarshal(src, OscalCatalogCatalogASSEMBLY.class);
        OscalCatalogCatalogASSEMBLY oscalRoot = obj.getValue();
        for (OscalCatalogGroupASSEMBLY g : oscalRoot.getGroup()) {
            TreeItem ti = new TreeItem("<I>Group:</I><B>" + g.getId() + "</B> " + g.getTitle().getContent());
            listParts(ti, g.getPart());
            for (OscalCatalogControlASSEMBLY c : g.getControl()) {
                TreeItem ti1 = new TreeItem("<I>Control:</I><B>" + c.getId() + "</B> " + c.getTitle().getContent());
                listParts(ti1, c.getPart());
                ti.child.add(ti1);
            }
            data.add(ti);
        }

        treeGrid.setItems(data, TreeItem::getChild);

        treeGrid.addComponentHierarchyColumn(item -> {
            Html test = new Html("<SPAN>"+item.value+"</SPAN>");            
            return test;
        }).setHeader("Name");

        treeGrid.addComponentColumn(item -> {
            Span test = new Span();
            test.setText(Integer.toString(item.getChild().size()));
            return test;
        }).setHeader("Count");

        add(treeGrid);
        add(new Button("hello"));
    }
}
