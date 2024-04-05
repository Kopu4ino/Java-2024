package edu.java.updateClients;

import edu.java.updateClients.updateDto.StackOverflowQuestionResponse;

public interface StackOverflowClient {
    StackOverflowQuestionResponse fetchQuestion(Long questionId);
}
