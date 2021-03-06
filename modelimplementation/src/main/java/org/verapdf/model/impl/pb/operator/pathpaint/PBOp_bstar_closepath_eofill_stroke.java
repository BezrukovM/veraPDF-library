package org.verapdf.model.impl.pb.operator.pathpaint;

import org.apache.pdfbox.cos.COSBase;
import org.verapdf.model.operator.Op_bstar_closepath_eofill_stroke;

import java.util.List;

/**
 * @author Timur Kamalov
 */
public class PBOp_bstar_closepath_eofill_stroke extends PBOpPathPaint implements Op_bstar_closepath_eofill_stroke {

    public static final String OP_BSTAR_CLOSEPATH_EOFILL_STROKE_TYPE = "Op_bstar_closepath_eofill_stroke";

    public PBOp_bstar_closepath_eofill_stroke(List<COSBase> arguments) {
        super(arguments);
        setType(OP_BSTAR_CLOSEPATH_EOFILL_STROKE_TYPE);
    }

}
