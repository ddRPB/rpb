/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2017 Tomas Skripcak
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dktk.dd.rpb.core.domain.edc.mapping;

import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;

import de.dktk.dd.rpb.core.util.Constants;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * MappingRecord domain entity
 * Specifies relationship between mapped source and target data item
 *
 * @author tomas@skripcak.net
 * @since 03 Feb 2015
 */
@Entity
@Table(name = "MAPPINGRECORD")
public class MappingRecord implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(MappingRecord.class);

    //endregion

    //region Members

    private Integer id; // pk, auto increment

    private String defaultValue;
    private String multiValueSeparator;
    private String dateFormatString;
    private String calculationString;
    private Integer priority;

    // Repeating keys (specific for CDISC ODM mapping)
    private String studyEventRepeatKey;
    private String itemGroupRepeatKey;

    // Theoretically multiple source values can be processed into one target (too complex for now)
    private AbstractMappedItem source;  // one to one
    // Theoretically one source value can be processed into multiple targets (too complex for now)
    private AbstractMappedItem target; // one to one

    private Map<String, String> mapping = new HashMap<>(); // mapping options

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public MappingRecord() {
        this.priority = 1;
    }

    @SuppressWarnings("unused")
    public MappingRecord(Integer primaryKey) {
        setId(primaryKey);
    }

    /**
     * Create a transient clone of mapping record entity based on another mapping record entity
     * @param anotherMappingRecord mapping record entity that should be cloned
     */
    public MappingRecord(MappingRecord anotherMappingRecord) {
        this.defaultValue = anotherMappingRecord.getDefaultValue();
        this.multiValueSeparator = anotherMappingRecord.getMultiValueSeparator();
        this.dateFormatString = anotherMappingRecord.getDateFormatString();
        this.priority = anotherMappingRecord.getPriority();
    }

    //endregion

    //region Properties

    //region ID

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mappingrecord_id_seq")
    @SequenceGenerator(name = "mappingrecord_id_seq", sequenceName = "mappingrecord_id_seq")
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    @Transient
    public boolean isIdSet() {
        return id != null;
    }

    //endregion

    //region DefaultValue

    @Size(max = 255)
    @Column(name = "DEFAULTVALUE")
    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    //endregion

    //region MultiValueSeparator

    @Size(max = 1)
    @Column(name = "MULTIVALUESEP", length = 1)
    public String getMultiValueSeparator() {
        return this.multiValueSeparator;
    }

    public void setMultiValueSeparator(String value) {
        this.multiValueSeparator = value;
    }

    //endregion

    //region DateFormatString

    @Size(max = 255)
    @Column(name = "DATEFORMATSTRING")
    public String getDateFormatString() {
        return this.dateFormatString;
    }

    public void setDateFormatString(String value) {
        this.dateFormatString = value;
    }

    //endregion

    //region Calculation

    @Size(max = 255)
    @Column(name = "CALCULATIONSTRING")
    public String getCalculationString() {
        return this.calculationString;
    }

    public void setCalculationString(String value) {
        this.calculationString = value;
    }

    //endregion

    //region Priority

    @Column(name = "PRIORITY")
    public Integer getPriority() {
        return this.priority;
    }

    public void setPriority(Integer value) {
        this.priority = value;
    }

    //endregion

    //region StudyEventRepeatKey

    @Size(max = 255)
    @Column(name = "SEREPEATKEY")
    public String getStudyEventRepeatKey() {
        return this.studyEventRepeatKey;
    }

    public void setStudyEventRepeatKey(String value) {
        this.studyEventRepeatKey = value;
    }

    //endregion

    //region ItemGroupRepeatKey

    @Size(max = 255)
    @Column(name = "IGREPEATKEY")
    public String getItemGroupRepeatKey() {
        return this.itemGroupRepeatKey;
    }

    public void setItemGroupRepeatKey(String value) {
        this.itemGroupRepeatKey = value;
    }

    //endregion

    //region Source - One MappingRecord has one SourceItem

    @OneToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name="SOURCEID")
    public AbstractMappedItem getSource() {
        return source;
    }

    public void setSource(AbstractMappedItem value) {
        this.source = value;
    }

    //endregion

    //region Target - One MappingRecord has one TargetItem

    @OneToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name="TARGETID")
    public AbstractMappedItem getTarget() {
        return target;
    }

    public void setTarget(AbstractMappedItem value) {
        this.target = value;
    }

    //endregion

    //region Mapping

    @ElementCollection(fetch = FetchType.EAGER) // this is a collection of primitives
    @MapKeyColumn(name="SOURCEVALUE") // column name for map "key"
    @Column(name="TARGETVALUE") // column name for map "value"
    @CollectionTable(name="CODEMAP", joinColumns=@JoinColumn(name="recordid"))
    public Map<String, String> getMapping() {
        return this.mapping;
    }

    public void setMapping(Map<String, String> map) {
        this.mapping = map;
    }

    public void addCodeMapping(String sourceCode, String targetCode) {
        this.mapping.put(sourceCode, targetCode);
    }

    public void removeCodeMapping(String sourceCode) {
        this.mapping.remove(sourceCode);
    }

    //endregion

    //endregion

    //region Methods

    public String process(String sourceValue, ItemDefinition itemMetaData) {
        String result = this.defaultValue;

        // Should any calculations be performed on source data
        // TODO: this is just quick an dirty, later implement parsing according to shunting-yard algorithm
        if (this.calculationString != null && !this.calculationString.isEmpty()) {

            Stack<String> operators = new Stack<>();
            Stack<String> output = new Stack<>();

            for (String token : calculationString.split(" ")) {
                if (token.equals("+")) {
                    operators.push(token);
                } else if (token.equals("-")) {
                    operators.push(token);
                } else if (token.equals("*")) {
                    operators.push(token);
                } else if (token.equals("/")) {
                    operators.push(token);
                } else if (NumberUtils.isNumber(token)) {
                    output.push(token);
                }
            }

            if (!operators.isEmpty() && !output.isEmpty()) {
                String operator = operators.pop();
                switch (operator) {
                    case "+": {
                        BigDecimal number1 = new BigDecimal(output.pop());
                        BigDecimal number = new BigDecimal(sourceValue);
                        sourceValue = number.add(number1).stripTrailingZeros().toPlainString();
                        break;
                    }
                    case "-": {
                        BigDecimal number1 = new BigDecimal(output.pop());
                        BigDecimal number = new BigDecimal(sourceValue);
                        sourceValue = number.subtract(number1).stripTrailingZeros().toPlainString();
                        break;
                    }
                    case "*": {
                        BigDecimal number1 = new BigDecimal(output.pop());
                        BigDecimal number = new BigDecimal(sourceValue);
                        sourceValue = number.multiply(number1).stripTrailingZeros().toPlainString();
                        break;
                    }
                    case "/": {
                        BigDecimal number1 = new BigDecimal(output.pop());
                        BigDecimal number = new BigDecimal(sourceValue);
                        sourceValue = number.divide(number1).stripTrailingZeros().toPlainString();
                        break;
                    }
                }
            }
        }

        // Nothing special use the data types in metadata to decide how to transform source value
        if (itemMetaData.getCodeListDef() == null && itemMetaData.getMultiSelectListDef() == null) {

            // Setup OC date format
            SimpleDateFormat ocFormat = new SimpleDateFormat(Constants.OC_DATEFORMAT);

            switch (itemMetaData.getDataType()) {
                case Constants.OC_INTEGER:
                    try {
                        result = Integer.valueOf(sourceValue).toString();
                    }
                    catch (NumberFormatException err) {
                        log.error("Cannot covert the source value to integer target value according to mapping", err);
                    }
                    break;
                case Constants.OC_DECIMAL:
                    try {
                        BigDecimal number = new BigDecimal(sourceValue);
                        result = number.stripTrailingZeros().toPlainString();
                    }
                    catch (NumberFormatException err) {
                        log.error("Cannot convert the source value to decimal target value according to mapping", err);
                    }
                    break;
                case Constants.OC_STRING:
                    result = sourceValue;
                    break;
                case Constants.OC_DATE:
                    try {
                        result = ocFormat.format(
                                new SimpleDateFormat(this.dateFormatString).parse(
                                        sourceValue
                                )
                        );
                    }
                    catch (ParseException e) {
                        result = this.parseDate(sourceValue);
                    }
                    break;
                case Constants.OC_PDATE:
                    // Even if the format is PDATE the data for import can be complete so try to parse it
                    try {
                        result = ocFormat.format(
                                new SimpleDateFormat(this.dateFormatString).parse(
                                        sourceValue
                                )
                        );
                    }
                    catch (ParseException e) {
                        log.info("Processing unknown PDATE type format: skipping");
                    }
                    break;
                case Constants.OC_FILE:
                    log.info("Processing FILE type: skipping");
                    break;
                default:
                    log.info("Processing unknown type: skipping");
                    break;
            }
        }
        else {
            // Code lists, if there is mapping defined use the mapping
            if (this.mapping != null && !this.mapping.isEmpty()) {
                if (itemMetaData.getCodeListDef() != null) {
                    String oneResult = this.mapping.get(sourceValue.trim());
                    if (itemMetaData.isCodeValid(oneResult)) {
                         result = oneResult;
                    }
                }
                else if (itemMetaData.getMultiSelectListDef() != null) {
                    for (String oneValue: sourceValue.trim().split(this.multiValueSeparator)) {
                        String oneResult = this.mapping.get(oneValue.trim());
                        if (itemMetaData.isCodeValid(oneResult)) {
                            if (result.equals(defaultValue) || "".equals(result)) {
                                result = oneResult;
                            }
                            else {
                                result += Constants.OC_MULTIVALSEP + oneResult;
                            }
                        }
                    }
                }
            }
            // When default value was defined it is already in result so do nothing
            else if (this.defaultValue != null && !this.defaultValue.isEmpty()) {
                // NOOP
            }
            // Otherwise use the codes directly (they should match) = no mapping specified
            else {
                if (itemMetaData.isCodeValid(sourceValue.trim())) {
                    result = sourceValue.trim();
                }
            }
        }

        return result;
    }

    // This is useful for GUI, data table does not support map data types
    public List<Map.Entry<String, String>> mappingAsList() {
        List<Map.Entry<String, String>>  result = null;

        if (this.mapping != null) {
            result = new ArrayList<>(this.mapping.entrySet());
        }

        return result;
    }

    // This is useful for GUI,to verify whether user already specified new code for certen coded value in target code list
    public Boolean recodeForTargetExists(String targetCodedValue) {
        if (targetCodedValue != null) {
            for (Map.Entry<String, String> codeEntry : this.mappingAsList()) {
                if (codeEntry.getValue().equals(targetCodedValue)) {
                    return Boolean.TRUE;
                }
            }

        }
        return Boolean.FALSE;
    }

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof MappingRecord && hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this entity instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .toString();
    }

    //endregion

    //region Private Methods

    private Boolean canPerformMapping(Map<String, String> mapping) {
        if(mapping == null) {
            throw new NullPointerException("mapping should not be null");
        }
        else if(mapping.isEmpty()) {
            throw new IllegalArgumentException("mapping should not be empty");
        }

        return true;
    }

    private String parseDate(String sourceDate) {
        // optionally change the separator
        String newSourceDate = sourceDate.replaceAll("\\D+", "/");

        for (String formats : "dd/MM/yy,yyyy/MM/dd,dd/MM/yyyy".split(",")) {
            try {
                SimpleDateFormat sourceFormat = new SimpleDateFormat(formats);
                sourceFormat.setLenient(false);
                SimpleDateFormat ocFormat = new SimpleDateFormat("yyyy-MM-dd");
                return ocFormat.format(sourceFormat.parse(newSourceDate));
            }
            catch (ParseException ignored) {
                // NOOP
            }
        }

        return "Error";
    }

    //endregion

}