package com.epam.training.gen.ai.service.imp;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Points;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.qdrant.client.ConditionFactory.matchKeyword;
import static io.qdrant.client.QueryFactory.nearest;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;

@Service
public class VectorDBService {


    private static final Logger log = LoggerFactory.getLogger(VectorDBService.class);
    @Autowired
    private QdrantClient qdrantClient;

    /**
     * Creates a new collection in Qdrant with specified vector parameters.
     *
     * @return
     * @throws ExecutionException   if the collection creation operation fails
     * @throws InterruptedException if the thread is interrupted during execution
     */
    public Collections.CollectionOperationResponse createCollection(String name) throws ExecutionException, InterruptedException {
        var result = qdrantClient.createCollectionAsync(name,
                        Collections.VectorParams.newBuilder()
                                .setDistance(Collections.Distance.Cosine)
                                .setSize(1536)
                                .build())
                .get();
        log.info("Collection was created: [{}]", result.getResult());
        return result;
    }

    public boolean collectionExists(String name) throws ExecutionException, InterruptedException {
        var result = qdrantClient.collectionExistsAsync(name).get();
        log.info("Collection [{}] exists: [{}]", name, result);
        return result;
    }

    /**
     * Saves the list of point structures (vectors) to the Qdrant collection.
     *
     * @param pointStructs the list of vectors to be saved
     * @throws InterruptedException if the thread is interrupted during execution
     * @throws ExecutionException   if the saving operation fails
     */
    public UpdateResult saveVector(String collectionName, ArrayList<PointStruct> pointStructs) throws InterruptedException, ExecutionException {
        var updateResult = qdrantClient.upsertAsync(collectionName, pointStructs).get();
        log.info(updateResult.getStatus().name());
        return updateResult;
    }

    public Collections.CollectionOperationResponse deleteCollection(String name) throws ExecutionException, InterruptedException {
        Collections.CollectionOperationResponse collectionOperationResponse = qdrantClient.deleteCollectionAsync(name).get();
        return collectionOperationResponse;
    }

    public List<Points.ScoredPoint> search(String collectionName, ArrayList<Float> allVectors) throws ExecutionException, InterruptedException {
        return qdrantClient
                .searchAsync(
                        Points.SearchPoints.newBuilder()
                                .setCollectionName(collectionName)
                                .addAllVector(allVectors)
                                .setWithPayload(enable(true))
                                .setLimit(3)
                                .build())
                .get();
    }

    public void addFilter() throws ExecutionException, InterruptedException {
        List<Points.ScoredPoint> searchResult =
                qdrantClient.queryAsync(Points.QueryPoints.newBuilder()
                        .setCollectionName("test_collection")
                        .setLimit(3)
                        .setFilter(Points.Filter.newBuilder().addMust(matchKeyword("city", "London")))
                        .setQuery(nearest(0.2f, 0.1f, 0.9f, 0.7f))
                        .setWithPayload(enable(true))
                        .build()).get();

        System.out.println(searchResult);
    }

}
