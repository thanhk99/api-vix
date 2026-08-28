package vix.local.api.modules.capital_source.api.v1.dto.response;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AuthorizationResponseDto {
    private UUID id;
    private Integer seqId;
    private UUID parentAuthId;
    private String authName;
    private String authPosition;
    private String authidNo;
    private LocalDate authissueDate;
    private String authedName;
    private String authedIdNo;
    private LocalDate authedIssueDate;
    private String issuePlace;
    private String authNo;
    private LocalDate effDate;
    private LocalDate ExpiryDate;
    private String authedPosition;
    private String phone;
    private String email;
}
