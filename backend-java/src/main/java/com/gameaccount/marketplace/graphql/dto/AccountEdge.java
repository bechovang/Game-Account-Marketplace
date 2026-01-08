package com.gameaccount.marketplace.graphql.dto;

import com.gameaccount.marketplace.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GraphQL Edge type for cursor-based pagination.
 * Contains a node (Account) and its cursor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEdge {

    /**
     * The account node.
     */
    private Account node;

    /**
     * The cursor for this edge (base64 encoded).
     */
    private String cursor;

    /**
     * Create an edge from an account.
     */
    public static AccountEdge of(Account account, String cursor) {
        return new AccountEdge(account, cursor);
    }
}
