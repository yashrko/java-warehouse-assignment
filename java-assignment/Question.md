# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
Yes. I would converge on one persistence boundary per aggregate and make transaction ownership explicit. The Store API uses PanacheEntity's static active-record methods while Product injects a PanacheRepository, and Warehouse has a domain port plus a repository adapter. The mix is workable but inconsistent: it complicates testing and makes transaction and query behavior harder to find. I would keep the domain model independent of Panache and use injected repositories behind ports for all three, with mapping at the persistence adapter. I would do this incrementally, preserving endpoint behavior and adding focused tests before moving each resource. I would not add abstraction to simple queries without a concrete domain or testability benefit.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
OpenAPI-first gives consumers a reviewable contract, consistent schemas and generated client/server types; it is useful for a public, multi-team API and helps detect breaking changes early. The cost is code-generation configuration, generated-code debugging and occasional friction when custom behavior or framework upgrades diverge from generator output. Handwritten endpoints are quick to evolve and easy to tailor, but the contract can drift, validation and error formats can vary, and consumers have less reliable documentation.

I would keep OpenAPI as the source of truth for externally consumed APIs, with contract linting and compatibility checks in CI, while implementing business rules in handwritten application services behind the generated resource interface. For internal or short-lived endpoints, a handwritten contract may be appropriate, but it should still have generated OpenAPI documentation and consistent validation/error conventions. I would avoid mixing styles without a clear ownership and publication policy.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
I would start with tests around business rules and data integrity because warehouse replacement, archive history, location limits and fulfillment caps carry the highest risk. Unit tests should cover each use case's boundaries with fake ports; repository tests should verify active/archive queries and mappings against the actual persistence provider; REST tests should cover status codes, JSON contracts and representative happy and failure paths. A small number of end-to-end tests should verify transaction behavior, database configuration and the most important cross-layer flows.

Prioritization should follow likelihood and impact of failure, then the cost of detecting it late. Tests should assert observable behavior rather than private implementation details, use deterministic fixtures, isolate database state and avoid duplicating the same scenario at every layer. CI should run formatting/compilation, unit and integration suites, publish line and branch coverage, and enforce a threshold on meaningful production code. Coverage is a signal, not the goal: review uncovered branches and mutation-sensitive rules, and require tests for bug fixes and new business behavior. Flaky tests, slow suite time and coverage trend should be monitored so the suite remains useful as the system grows.
```
