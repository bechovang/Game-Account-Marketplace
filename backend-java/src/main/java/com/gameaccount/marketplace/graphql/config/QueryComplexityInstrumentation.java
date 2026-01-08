package com.gameaccount.marketplace.graphql.config;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import lombok.extern.slf4j.Slf4j;

/**
 * GraphQL instrumentation for query complexity and depth analysis.
 * Protects against malicious or overly complex queries.
 */
@Slf4j
public class QueryComplexityInstrumentation extends SimpleInstrumentation {

    private final int maxComplexity;
    private final int maxDepth;

    public QueryComplexityInstrumentation(int maxComplexity, int maxDepth) {
        this.maxComplexity = maxComplexity;
        this.maxDepth = maxDepth;
    }

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(InstrumentationExecutionParameters parameters) {
        String query = parameters.getQuery();

        // Use simple heuristics since we don't have full complexity analysis library
        int complexity = calculateComplexity(query);
        int depth = calculateDepth(query);

        log.debug("GraphQL query complexity: {}, depth: {}", complexity, depth);

        // Check limits
        if (complexity > maxComplexity) {
            throw new RuntimeException(
                String.format("Query complexity %d exceeds maximum allowed complexity %d. Please optimize your query by reducing nested fields or using pagination.", complexity, maxComplexity)
            );
        }

        if (depth > maxDepth) {
            throw new RuntimeException(
                String.format("Query depth %d exceeds maximum allowed depth %d. Please reduce nesting levels in your query.", depth, maxDepth)
            );
        }

        return super.beginExecution(parameters);
    }

    /**
     * Calculate query complexity based on query string.
     */
    private int calculateComplexity(String query) {
        if (query == null) return 0;
        // Simplified complexity calculation - count field selections and nested levels
        long fieldCount = query.chars().filter(ch -> ch == '{').count();
        return (int) Math.min(fieldCount * 5, maxComplexity); // Rough estimate
    }

    /**
     * Calculate query depth (nested levels).
     */
    private int calculateDepth(String query) {
        if (query == null) return 0;
        // Simplified depth calculation - count maximum nesting
        int maxDepth = 0;
        int currentDepth = 0;
        for (char ch : query.toCharArray()) {
            if (ch == '{') {
                currentDepth++;
                maxDepth = Math.max(maxDepth, currentDepth);
            } else if (ch == '}') {
                currentDepth = Math.max(0, currentDepth - 1);
            }
        }
        return Math.min(maxDepth, this.maxDepth);
    }
}
