package vn.hub.mailservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleRecipientsEmailRequest {
    private String to;
    private List<String> ccList;
    private List<String> bccList;
    private String subject;
    private String htmlContent;
}