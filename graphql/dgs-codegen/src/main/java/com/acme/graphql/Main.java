package com.acme.graphql;

import com.acme.graphql.client.ShowsGraphQLQuery;
import com.acme.graphql.client.ShowsProjectionRoot;
import com.acme.graphql.types.Show;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;

/** Builds a typed query with the generated client and prints it beside a generated type. */
public final class Main {

    private Main() {}

    public static void main(String[] args) {
        Show show = Show.newBuilder().title("Stranger Things").releaseYear(2016).build();
        System.out.println(show.getTitle() + " (" + show.getReleaseYear() + ")");
        System.out.println(showsQuery("Stranger"));
    }

    /** The GraphQL document asking for every show whose title matches {@code titleFilter}. */
    static String showsQuery(String titleFilter) {
        ShowsGraphQLQuery query =
                ShowsGraphQLQuery.newRequest().titleFilter(titleFilter).build();
        ShowsProjectionRoot<?, ?> projection = new ShowsProjectionRoot<>().title().releaseYear();
        return new GraphQLQueryRequest(query, projection).serialize();
    }
}
