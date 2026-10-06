# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
Before setting scope, I would map the cost objects and the decisions the tool must support: are costs needed by warehouse, store, product, order, activity, or combinations of these? I would clarify the accounting period, currency, chart-of-accounts mapping, allocation rules, required accuracy, and whether the output is operational guidance or a finance-controlled ledger. Labor needs shift and activity data (receiving, picking, packing); inventory needs carrying, shrinkage and obsolescence costs; transport needs lane, carrier, fuel and shipment data; overhead needs a defensible driver such as area, labor hours or throughput. Shared costs should retain both the source amount and allocation rule so users can reconcile allocated totals to the general ledger.

I would ask how indirect costs should be allocated, how returns and rework are treated, how often rates change, which systems are authoritative, and who approves adjustments. In practice, poor master data and inconsistent time or volume units usually create more variance than the arithmetic. I would begin with a small set of agreed cost drivers, reconcile a sample period end-to-end with Finance, and expose data freshness and confidence alongside the reported unit cost.

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
I would establish a baseline by cost per shipped unit/order, on-time-in-full rate, labor productivity, utilization, inventory aging, damage/returns and transport cost per lane. Candidate changes include slotting fast movers nearer pick faces, reducing touches and travel, improving labor and volume forecasts, consolidating shipments where service permits, renegotiating carrier lanes, reducing avoidable inventory and energy use, and balancing capacity across locations. Each opportunity needs a quantified expected saving, implementation cost, time to benefit, operational risk and service-quality guardrail.

I would rank ideas by validated annual benefit, confidence, effort and reversibility, then pilot the highest-value low-risk changes against a control or a pre-agreed baseline. Track both cost and service metrics to prevent savings from shifting cost downstream or increasing late deliveries. Roll out only when the measured result is repeatable; assign an owner, document the process change, and review realized savings after the pilot and at later periods. Forecasts and assumptions should be visible so fuel prices, wage changes or demand mix do not get mistaken for the effect of an operational change.

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
Integration joins operational events to authoritative financial actuals, allowing managers to compare expected and incurred costs, close periods with fewer manual reconciliations and detect overspend while there is still time to act. It also creates a common view of commitments, accruals and actuals by cost center and operational unit. Before choosing an interface I would identify the ERP and warehouse systems, their owners, identifiers, supported latency, volume, correction policy, security model and close-calendar requirements.

I would use stable identifiers and explicit mappings for warehouse, store, account, currency and period. Durable events or an outbox with idempotent consumers are safer than a synchronous dual write; retries, dead-letter handling, reconciliation totals and replay tools make gaps recoverable. For near-real-time reporting, stream or publish operational events and post financial transactions through the ERP's supported API; use scheduled reconciliation against the ledger as the financial control. Monitor freshness, duplicates, rejected mappings and amount mismatches, preserve audit history, and agree with Finance which source wins when values differ.

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
Budgets translate service and demand plans into labor, space, inventory and transport capacity. Forecasts reveal when demand, wage rates, carrier prices or capacity constraints are likely to move actual costs away from plan, so leaders can adjust before the period closes. I would first determine planning horizons, granularity, accountable owners, update cadence, approval controls and the cost categories that materially affect decisions.

The model should combine demand by product and channel with seasonality, promotions, service targets, labor rates and productivity, warehouse capacity/utilization, inventory policy, carrier lanes and contractual rates, inflation and known projects. It should support a driver-based base case plus upside/downside scenarios, keep assumptions versioned, and compare forecast, budget and actuals using the same definitions. Forecast accuracy and bias should be measured at the level where decisions are made; users need to see drivers and explain overrides. A phased release should begin with high-impact cost lines and a rolling forecast, then expand as data quality and ownership mature.

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
Replacement should be treated as a controlled transition with two separate identities: the enduring business unit and the historical warehouse instance. Preserve the old warehouse's immutable cost transactions, effective dates, approved allocations, budget versions and close status, while marking the instance as archived and linking it to the successor. Reusing a business unit code must not overwrite history or make old costs appear to belong to the new operation. Finance should define the effective cutover, treatment of open purchase orders, inventory, accruals, depreciation and transition costs, plus whether comparisons are restated or shown as a successor chain.

Before cutover I would baseline the old site's run rate and remaining commitments, then build a transition budget for duplicated labor, stock transfer, transport, commissioning and temporary capacity. The new warehouse's forecast should distinguish recurring costs from one-time migration costs and retain comparable service and volume drivers. Reconcile balances and inventory at cutover, assign approval for variances, and monitor both sites until obligations close. Key information includes the planned date, expected overlap, capacity and service impact, cost-center mapping, contract exit/entry costs, and the reporting view stakeholders need for year-over-year comparison.

Across all scenarios, I would set boundaries only after identifying the decisions and business outcomes, users and owners, authoritative systems, data quality and latency, scale and service constraints, regulatory/audit needs, existing processes, dependencies, measurable success criteria, and a smallest valuable pilot. That discovery keeps the technical scope tied to measurable cost and service improvements.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
