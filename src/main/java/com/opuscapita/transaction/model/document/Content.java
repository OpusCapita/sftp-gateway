package com.opuscapita.transaction.model.document;

import com.opuscapita.transaction.model.Tx;
import com.opuscapita.transaction.utils.TxUtils;
import lombok.Data;

import java.util.Objects;

@Data
public class Content implements Tx {

    private String reference;
    private String name;
    private String refType;
    private String category;
    private int iteration = 0;

    public Content(
            String _reference,
            String _name,
            String _refType,
            String _category,
            int _iteration
    ) {
        if (Objects.isNull(_reference))
            throw new NullPointerException("Reference is null");
        if (Objects.isNull(_name))
            throw new NullPointerException("Name is null");
        if (Objects.isNull(_refType))
            throw new NullPointerException("RefType is null");
        if (Objects.isNull(_category))
            throw new NullPointerException("Category is null");
        if (Objects.isNull(_iteration))
            throw new NullPointerException("Iteration is null");
        this.setReference(_reference);
        this.setName(_name);
        this.setRefType(_refType);
        this.setCategory(_category);
        this.setIteration(_iteration);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(TxUtils.attributeAsJson("reference", Objects.requireNonNull(this.getReference(), "--- no value ---")));
        builder.append(TxUtils.attributeAsJson("refType", Objects.requireNonNull(this.getRefType(), "--- no value ---")));
        builder.append(TxUtils.attributeAsJson("name", Objects.requireNonNull(this.getName(), "--- no value ---")));
        builder.append(TxUtils.attributeAsJson("category", Objects.requireNonNull(this.getCategory(), "--- no value ---")));
        builder.append(TxUtils.attributeAsJson("iteration", this.getIteration(), false));
        builder.append("}");
        return builder.toString();
    }
}
