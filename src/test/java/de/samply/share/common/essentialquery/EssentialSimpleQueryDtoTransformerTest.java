package de.samply.share.common.essentialquery;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import de.samply.share.essentialquery.EssentialSimpleFieldDto;
import de.samply.share.essentialquery.EssentialSimpleQueryDto;
import de.samply.share.essentialquery.EssentialSimpleValueDto;
import de.samply.share.essentialquery.EssentialValueType;
import de.samply.share.query.common.MdrFieldDto;
import de.samply.share.query.entity.SimpleQueryDto;
import de.samply.share.query.enums.SimpleValueCondition;
import de.samply.share.query.field.AbstractQueryFieldDto;
import de.samply.share.query.field.FieldDateDto;
import de.samply.share.query.field.FieldDateTimeDto;
import de.samply.share.query.field.FieldDecimalDto;
import de.samply.share.query.field.FieldIntegerDto;
import de.samply.share.query.field.FieldPermittedValueDto;
import de.samply.share.query.field.FieldStringDto;
import de.samply.share.query.value.AbstractQueryValueDto;
import de.samply.share.query.value.ValueDateDto;
import de.samply.share.query.value.ValueDateTimeDto;
import de.samply.share.query.value.ValueDecimalDto;
import de.samply.share.query.value.ValueIntegerDto;
import de.samply.share.query.value.ValuePermittedValuesDto;
import de.samply.share.query.value.ValueStringDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EssentialSimpleQueryDtoTransformerTest {

  private EssentialSimpleQueryDtoTransformer transformer;

  @BeforeEach
  void init() {
    transformer = new EssentialSimpleQueryDtoTransformer();
  }

  @Test
  void testTransform_null() {
    EssentialSimpleQueryDto actual = transformer.transform(null);

    assertThat("null should transform to an empty EssentialSimpleQueryDto",
        actual.getFieldDtos().size(), is(0));
  }

  @Test
  void testTransform_empty() {
    EssentialSimpleQueryDto actual = transformer.transform(null);

    assertThat("empty SimpleQueryDto should transform to an empty EssentialSimpleQueryDto",
        actual.getFieldDtos().size(), is(0));
  }

  @Test
  void testTransform_donorField() {
    SimpleQueryDto simpleQueryDto = createSimpleDtoWithDonorField();

    EssentialSimpleQueryDto actual = transformer.transform(simpleQueryDto);

    assertThat("SimpleQueryDto has one donor field", actual.getFieldDtos().size(), is(1));

    EssentialSimpleFieldDto fieldDto = actual.getFieldDtos().get(0);
    assertThat(fieldDto.getUrn(), is("urn1"));
    assertThat(fieldDto.getValueType(), is(EssentialValueType.DECIMAL));
    assertThat(fieldDto.getValueDtos().size(), is(2));

    EssentialSimpleValueDto valueDto1 = fieldDto.getValueDtos().get(0);
    assertThat(valueDto1.getCondition(), is(SimpleValueCondition.NOT_EQUALS));
    assertThat(valueDto1.getValue(), is("1.0"));
    assertThat(valueDto1.getMaxValue(), is("101.0"));

    EssentialSimpleValueDto valueDto2 = fieldDto.getValueDtos().get(1);
    assertThat(valueDto2.getCondition(), is(SimpleValueCondition.NOT_EQUALS));
    assertThat(valueDto2.getValue(), is("2.0"));
    assertThat(valueDto2.getMaxValue(), is("102.0"));
  }

  SimpleQueryDto createSimpleDtoWithDonorField() {
    SimpleQueryDto simpleQueryDto = new SimpleQueryDto();

    FieldDecimalDto fieldDecimalDto = createField("urn1", 1d, 2d);

    simpleQueryDto.getDonorDto().getFieldsDto().add(fieldDecimalDto);

    return simpleQueryDto;
  }

  @Test
  void testTransform_sampleField() {
    SimpleQueryDto simpleQueryDto = createSimpleDtoWithSampleField();

    EssentialSimpleQueryDto actual = transformer.transform(simpleQueryDto);

    assertThat("SimpleQueryDto has one sample field", actual.getFieldDtos().size(), is(1));

    EssentialSimpleFieldDto fieldDto = actual.getFieldDtos().get(0);
    assertThat(fieldDto.getUrn(), is("urn1"));
    assertThat(fieldDto.getValueType(), is(EssentialValueType.DECIMAL));
    assertThat(fieldDto.getValueDtos().size(), is(2));

    EssentialSimpleValueDto valueDto1 = fieldDto.getValueDtos().get(0);
    assertThat(valueDto1.getCondition(), is(SimpleValueCondition.NOT_EQUALS));
    assertThat(valueDto1.getValue(), is("1.0"));
    assertThat(valueDto1.getMaxValue(), is("101.0"));

    EssentialSimpleValueDto valueDto2 = fieldDto.getValueDtos().get(1);
    assertThat(valueDto2.getCondition(), is(SimpleValueCondition.NOT_EQUALS));
    assertThat(valueDto2.getValue(), is("2.0"));
    assertThat(valueDto2.getMaxValue(), is("102.0"));
  }

  SimpleQueryDto createSimpleDtoWithSampleField() {
    SimpleQueryDto simpleQueryDto = new SimpleQueryDto();

    FieldDecimalDto fieldDecimalDto = createField("urn1", 1d, 2d);

    simpleQueryDto.getSampleDto().getFieldsDto().add(fieldDecimalDto);

    return simpleQueryDto;
  }

  @Test
  void testTransform_sampleAndDonorFields() {
    SimpleQueryDto simpleQueryDto = createSimpleDtoWithSampleAndDonorFields();

    EssentialSimpleQueryDto actual = transformer.transform(simpleQueryDto);

    assertThat("SimpleQueryDto has one donor and two sample fields", actual.getFieldDtos().size(),
        is(3));
  }


  SimpleQueryDto createSimpleDtoWithSampleAndDonorFields() {
    SimpleQueryDto simpleQueryDto = new SimpleQueryDto();

    FieldDecimalDto fieldDecimalDtoDonor = createField("urn1", 1d, 2d);
    FieldDecimalDto fieldDecimalDtoSample1 = createField("urn2", 2d, 3d);
    FieldDecimalDto fieldDecimalDtoSample2 = createField("urn2", 3d, 4d);

    simpleQueryDto.getDonorDto().getFieldsDto().add(fieldDecimalDtoDonor);
    simpleQueryDto.getSampleDto().getFieldsDto().add(fieldDecimalDtoSample1);
    simpleQueryDto.getSampleDto().getFieldsDto().add(fieldDecimalDtoSample2);

    return simpleQueryDto;
  }

  private FieldDecimalDto createField(String urn, Double... values) {
    FieldDecimalDto fieldDto = new FieldDecimalDto();

    fieldDto.setMdrFieldDto(createMdrType(urn));

    for (Double value : values) {
      addValueDto(fieldDto, value);
    }

    return fieldDto;
  }

  private MdrFieldDto createMdrType(String urn) {
    MdrFieldDto mdrFieldDto = new MdrFieldDto();

    mdrFieldDto.setUrn(urn);

    return mdrFieldDto;
  }

  private void addValueDto(FieldDecimalDto fieldDto, Double value) {
    ValueDecimalDto valueDto = new ValueDecimalDto();

    valueDto.setValue(value);
    valueDto.setMaxValue(value + 100);
    valueDto.setCondition(SimpleValueCondition.NOT_EQUALS);

    fieldDto.getValuesDto().add(valueDto);
  }

  @Test
  void testTransform_STRING() {
    ValueStringDto valueDto = new ValueStringDto();
    FieldStringDto fieldDto = new FieldStringDto();

    SimpleQueryDto simpleQueryDto = createSimpleQueryDto(valueDto, fieldDto);
    EssentialSimpleQueryDto actual = transformer.transform(simpleQueryDto);

    checkAssertionForType(actual, EssentialValueType.STRING);
  }

  @Test
  void testTransform_PERMITTEDVALUE() {
    ValuePermittedValuesDto valueDto = new ValuePermittedValuesDto();
    FieldPermittedValueDto fieldDto = new FieldPermittedValueDto();

    SimpleQueryDto simpleQueryDto = createSimpleQueryDto(valueDto, fieldDto);
    EssentialSimpleQueryDto actual = transformer.transform(simpleQueryDto);

    checkAssertionForType(actual, EssentialValueType.PERMITTEDVALUE);
  }

  @Test
  void testTransform_INTEGER() {
    ValueIntegerDto valueDto = new ValueIntegerDto();
    FieldIntegerDto fieldDto = new FieldIntegerDto();

    SimpleQueryDto simpleQueryDto = createSimpleQueryDto(valueDto, fieldDto);
    EssentialSimpleQueryDto actual = transformer.transform(simpleQueryDto);

    checkAssertionForType(actual, EssentialValueType.INTEGER);
  }

  @Test
  void testTransform_DECIMAL() {
    ValueDecimalDto valueDto = new ValueDecimalDto();
    FieldDecimalDto fieldDto = new FieldDecimalDto();

    SimpleQueryDto simpleQueryDto = createSimpleQueryDto(valueDto, fieldDto);
    EssentialSimpleQueryDto actual = transformer.transform(simpleQueryDto);

    checkAssertionForType(actual, EssentialValueType.DECIMAL);
  }

  @Test
  void testTransform_DATE() {
    ValueDateDto valueDto = new ValueDateDto();
    FieldDateDto fieldDto = new FieldDateDto();

    SimpleQueryDto simpleQueryDto = createSimpleQueryDto(valueDto, fieldDto);
    EssentialSimpleQueryDto actual = transformer.transform(simpleQueryDto);

    checkAssertionForType(actual, EssentialValueType.DATE);
  }

  @Test
  void testTransform_DATETIME() {
    ValueDateTimeDto valueDto = new ValueDateTimeDto();
    FieldDateTimeDto fieldDto = new FieldDateTimeDto();

    SimpleQueryDto simpleQueryDto = createSimpleQueryDto(valueDto, fieldDto);
    EssentialSimpleQueryDto actual = transformer.transform(simpleQueryDto);

    checkAssertionForType(actual, EssentialValueType.DATETIME);
  }

  private void checkAssertionForType(EssentialSimpleQueryDto actual,
      EssentialValueType expectedType) {
    assertThat("SimpleQueryDto has one donor ", actual.getFieldDtos().size(), is(1));

    EssentialValueType actualType = actual.getFieldDtos().get(0).getValueType();
    assertThat("Type must be " + expectedType, actualType, is(expectedType));
  }

  private <T_FIELD,
      T_VALUE_DTO extends AbstractQueryValueDto<T_FIELD>,
      T_FIELD_DTO extends AbstractQueryFieldDto<T_FIELD, T_VALUE_DTO>> SimpleQueryDto createSimpleQueryDto(
      T_VALUE_DTO valueDto,
      T_FIELD_DTO fieldDto) {
    fieldDto.setMdrFieldDto(createMdrType("urn1"));
    fieldDto.getValuesDto().add(valueDto);

    SimpleQueryDto simpleQueryDto = new SimpleQueryDto();
    simpleQueryDto.getDonorDto().getFieldsDto().add(fieldDto);
    return simpleQueryDto;
  }
}
