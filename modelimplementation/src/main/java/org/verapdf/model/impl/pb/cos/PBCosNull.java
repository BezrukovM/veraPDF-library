package org.verapdf.model.impl.pb.cos;

import org.apache.pdfbox.cos.COSNull;
import org.verapdf.model.coslayer.CosNull;

/**
 * Created by Evgeniy Muravitskiy on 4/28/15.
 * <p>
 *     Current class is representation of CosNull interface of abstract model.
 *     This class is analogue of COSNull in pdfbox.
 * </p>
 */
public final class PBCosNull extends PBCosObject implements CosNull {

    /** PDF null object
     */
    public final static CosNull NULL = new PBCosNull(COSNull.NULL);

    private PBCosNull(COSNull nil) {
        super(nil);
        setType("CosNull");
    }
}