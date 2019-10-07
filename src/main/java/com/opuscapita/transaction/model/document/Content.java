package com.opuscapita.transaction.model.document;

import com.opuscapita.transaction.model.Tx;
import lombok.Data;

@Data
public class Content implements Tx {

    @Override
    public String toString() {
        return "";
    }
}
