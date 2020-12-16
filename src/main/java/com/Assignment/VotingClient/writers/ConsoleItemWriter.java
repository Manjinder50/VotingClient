package com.Assignment.VotingClient.writers;

import com.Assignment.VotingClient.Response.FinalResponse;
import com.Assignment.VotingClient.Response.VoteCountResponse;
import com.Assignment.VotingClient.models.VotingInfo;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConsoleItemWriter<T> implements ItemWriter<T> {
    List<VotingInfo> votingInfoList = new ArrayList<>();
    @Override
    public void write(List<? extends T> items) throws Exception {
        /*for (T item : items) {
            System.out.println(item);
        }*/

        votingInfoList.addAll((Collection<? extends VotingInfo>) items);

        //Todo:Configure rabbit mq and push the response
        votingInfoList.forEach(System.out::println);

        Map<String, Integer> countMap = votingInfoList.stream()
                .collect(Collectors.groupingBy(VotingInfo::getParty, Collectors.summingInt(e -> 1)));

        countMap.entrySet().forEach(System.out::println);

        //Create multiple objects of VoteCountResponse for each entry of the countMap and then add those objects
        //to the list

        List<VoteCountResponse> finalList = countMap.entrySet()
                .stream()
                .map(entry -> new VoteCountResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        finalList.forEach(System.out::println);

        FinalResponse finalResponse = FinalResponse.builder()
                                                   .finalResponse(finalList)
                                                   .build();

//        finalResponse.getFinalResponse().forEach(System.out::println);


    }


}
