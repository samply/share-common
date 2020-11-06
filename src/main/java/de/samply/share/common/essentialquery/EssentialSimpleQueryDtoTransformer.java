package de.samply.share.common.essentialquery;

import de.samply.share.essentialquery.EssentialSimpleFieldDto;
import de.samply.share.essentialquery.EssentialSimpleQueryDto;
import de.samply.share.essentialquery.EssentialSimpleValueDto;
import de.samply.share.essentialquery.EssentialValueType;
import de.samply.share.query.entity.SimpleQueryDto;
import de.samply.share.query.field.AbstractQueryFieldDto;
import de.samply.share.query.field.FieldDateDto;
import de.samply.share.query.field.FieldDateTimeDto;
import de.samply.share.query.field.FieldDecimalDto;
import de.samply.share.query.field.FieldIntegerDto;
import de.samply.share.query.field.FieldPermittedValueDto;
import de.samply.share.query.value.AbstractQueryValueDto;
import java.util.List;

public class EssentialSimpleQueryDtoTransformer {

  /**
   * Todo.
   * @param sourceDto Todo
   * @return Todo
   */
  public EssentialSimpleQueryDto transform(SimpleQueryDto sourceDto) {
    if (sourceDto == null) {
      return new EssentialSimpleQueryDto();
    }

    EssentialSimpleQueryDto resultDto = new EssentialSimpleQueryDto();

    addFieldDtos(resultDto, sourceDto.getSampleDto().getFieldsDto());
    addFieldDtos(resultDto, sourceDto.getDonorDto().getFieldsDto());

    return resultDto;
  }

  private void addFieldDtos(EssentialSimpleQueryDto resultDto,
      List<AbstractQueryFieldDto<?, ?>> fieldsDto) {
    for (AbstractQueryFieldDto<?, ?> sourceFieldDto : fieldsDto) {
      EssentialSimpleFieldDto resultFieldDto = new EssentialSimpleFieldDto();

      resultFieldDto.setUrn(sourceFieldDto.getUrn());
      resultFieldDto.setValueType(calculateValueType(sourceFieldDto));

      addValueDtos(sourceFieldDto, resultFieldDto);

      resultDto.getFieldDtos().add(resultFieldDto);
    }
  }

  private void addValueDtos(AbstractQueryFieldDto<?, ?> sourceFieldDto,
      EssentialSimpleFieldDto resultFieldDto) {
    for (AbstractQueryValueDto<?> sourceValueDto : sourceFieldDto.getValuesDto()) {
      EssentialSimpleValueDto resultValueDto = new EssentialSimpleValueDto();

      resultValueDto.setCondition(sourceValueDto.getCondition());
      resultValueDto.setValue(sourceValueDto.getValueAsXmlString());
      resultValueDto.setMaxValue(sourceValueDto.getMaxValueAsXmlString());

      resultFieldDto.getValueDtos().add(resultValueDto);
    }
  }

  private EssentialValueType calculateValueType(AbstractQueryFieldDto<?, ?> sourceFieldDto) {
    if (sourceFieldDto instanceof FieldDateDto) {
      return EssentialValueType.DATE;
    }
    if (sourceFieldDto instanceof FieldDateTimeDto) {
      return EssentialValueType.DATETIME;
    }

    if (sourceFieldDto instanceof FieldDecimalDto) {
      return EssentialValueType.DECIMAL;
    }

    if (sourceFieldDto instanceof FieldIntegerDto) {
      return EssentialValueType.INTEGER;
    }

    if (sourceFieldDto instanceof FieldPermittedValueDto) {
      return EssentialValueType.PERMITTEDVALUE;
    }

    return EssentialValueType.STRING;
  }
}
