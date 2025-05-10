package it.polimi.ingsw.is25am49.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * PairMixIn tells Jackson how to create javafx.util.Pair objects from JSON.
 * Since javafx.util.Pair lacks a default constructor and Jackson's special instructions (and we can't change it),
 * the PairMixIn externally adds these instructions (like @JsonCreator)
 * so Jackson knows to use Pair's existing constructor (Pair(key, value)) for deserialization.
 *
 * DO NOT TOUCH THIS CLASS
 */
abstract class PairMixIn<K, V> {
    @JsonCreator
    PairMixIn(@JsonProperty("key") K key, @JsonProperty("value") V value) { }

    @JsonProperty("key")
    abstract K getKey();

    @JsonProperty("value")
    abstract V getValue();
}
