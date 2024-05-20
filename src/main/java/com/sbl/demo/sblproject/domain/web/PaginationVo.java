package com.sbl.demo.sblproject.domain.web;

import com.sbl.demo.sblproject.domain.web.dto.SrchDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaginationVo extends SrchDto {

    private Integer pageNum;
    private Integer pageSize;
    private Integer start;
    private Integer length;
}
