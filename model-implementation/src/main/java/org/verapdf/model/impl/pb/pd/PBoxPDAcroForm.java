package org.verapdf.model.impl.pb.pd;

import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.verapdf.model.baselayer.Object;
import org.verapdf.model.pdlayer.PDAcroForm;
import org.verapdf.model.pdlayer.PDFormField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PDF interactive form
 *
 * @author Evgeniy Muravitskiy
 */
public class PBoxPDAcroForm extends PBoxPDObject implements PDAcroForm {

	public static final String ACRO_FORM_TYPE = "PDAcroForm";

    public static final String FORM_FIELDS = "formFields";

    public PBoxPDAcroForm(
            org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm simplePDObject) {
        super(simplePDObject, ACRO_FORM_TYPE);
    }

    @Override
    public Boolean getNeedAppearances() {
        return Boolean
                .valueOf(((org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm) this.simplePDObject)
                        .getNeedAppearances());
    }

    @Override
    public List<? extends Object> getLinkedObjects(String link) {
        if (FORM_FIELDS.equals(link)) {
            return this.getFormFields();
        }
        return super.getLinkedObjects(link);
    }

    private List<PDFormField> getFormFields() {
        List<PDField> fields = ((org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm) this.simplePDObject)
                .getFields();
        for (PDField field : fields) {
			List<PDFormField> formFields =
					new ArrayList<>(MAX_NUMBER_OF_ELEMENTS);
			formFields.add(new PBoxPDFormField(field));
			return Collections.unmodifiableList(formFields);
        }
        return Collections.emptyList();
    }
}
