# OWWS&ECS Backend Debugging Log

**Project:** Offline Wearable Worker Safety & Emergency Communication System (OWWS&ECS)  
**Component:** Java Spring Boot Backend  
**Date:** 15 October 2026  
**Status:** Fake data initialization fixed. API endpoint differentiation pending.

## 1. Issue: Empty API Responses

### Observed behavior

The backend started successfully, but the REST endpoints returned empty responses.

| Endpoint | Initial response |
|---|---|
| `GET /api/status` | `{}` |
| `GET /api/events` | `[]` |
| `GET /api/workers/count` | `0` |

### Root cause

The backend uses an in-memory `StateManager` to maintain worker states and packet history. However, no sensor packets were being inserted into it during application startup.

The project included a `FakeDataRunner.java` file intended to populate the backend with test packets, but the file was completely empty.

Consequently, the `StateManager` contained no worker records, resulting in empty API responses.

### Additional configuration issue

The configuration file had previously been named `application.properties.txt` because Windows had hidden the file extension.

It was renamed to:

`backend/src/main/resources/application.properties`

The file contained:

```properties
server.port=8080
spring.application.name=owwsecs-backend

# Enable fake packet injection for testing
owwsecs.simulator.enabled=true
```

Correcting the filename resolved the configuration naming issue, but did not populate the state manager because `FakeDataRunner.java` was still empty.

## 2. Fix: Implement Fake Data Injection

The `FakeDataRunner.java` file was populated with a Spring Boot `CommandLineRunner` implementation.

The runner:

1. Uses constructor injection to obtain the `StateManager`.
2. Defines three fake sensor packets.
3. Parses each packet using `PacketParser.parse1sPacket()`.
4. Checks whether parsing succeeds.
5. Inserts valid packets using `stateManager.updateState(packet)`.
6. Prints diagnostic messages and the final worker count.

### Test packets

The current test packets use the following CSV structure:

`worker_id,packet_id,status,gas_level,temperature,flags,hop_count,relay_id`

```text
1,105,2,420,28,1,0,0
2,106,1,780,35,0,1,1
3,107,3,350,29,0,2,2
```

These packets represent three distinct workers with different status values.

### Expected startup behavior

The runner prints messages confirming its execution, packet acceptance, and worker count.

The expected final count is:

```text
Worker count: 3
```

### Verification

After saving the Java file and restarting the application, the fake packets were successfully accepted and the APIs began returning data.

This confirmed that the problem was the missing fake data initialization code rather than a complete failure of the Spring Boot application.

**Result: Resolved.**

## 3. Follow-up Issue: Similar API Responses

After fake data injection was fixed, the following endpoints returned data:

- `GET /api/workers`
- `GET /api/status`
- `GET /api/events`

However, their responses appeared similar.

Inspection of `ApiController.java` revealed the following implementations.

### Workers endpoint

```java
@GetMapping("/workers")
public Map<Integer, SensorPacket> getWorkers() {
    return stateManager.getAllWorkerStates();
}
```

Returns the latest stored packet for each known worker.

### Status endpoint

```java
@GetMapping("/status")
public Map<Integer, SensorPacket> getStatus() {
    return stateManager.getAllWorkerStates();
}
```

Calls the same `StateManager` method as the workers endpoint. Therefore, both endpoints are expected to return the same underlying worker-state data.

### Events endpoint

```java
@GetMapping("/events")
public List<SensorPacket> getEvents() {
    return stateManager.getPacketHistory();
}
```

Returns packet history, documented in the controller as the last ten received packets, newest first.

Because the fake runner inserts one packet for each of three workers, the event history and latest worker states contain overlapping data. Their JSON structures differ: the workers endpoint returns a map, while the events endpoint returns a list.

### Intended endpoint responsibilities

| Endpoint | Intended responsibility |
|---|---|
| `/api/workers` | Return the latest sensor data for each known worker |
| `/api/status` | Provide a concise summary of worker conditions and active hazards |
| `/api/events` | Return recent packet or event history |

The status summary has not yet been implemented. Its exact response structure and alert-counting rules remain to be defined.

**Result: Pending.**

## 4. Additional Issue: Port 8080 Conflict

A subsequent application startup attempt failed with:

```text
Web server failed to start. Port 8080 was already in use.
```

This indicates that another process was already listening on port `8080`, potentially an earlier backend instance.

The existing instance may still serve browser requests even when a new startup attempt fails.

### Recommended resolution

1. Identify the terminal or process running the existing backend.
2. Stop that instance using `Ctrl+C`, or terminate the identified process if necessary.
3. Start a single backend instance.
4. Confirm that the startup logs report successful server initialization.

The port conflict is separate from the original empty-response issue.

## 5. Current Implementation Status

| Item | Status |
|---|---|
| Spring Boot application startup | Working |
| Configuration filename correction | Completed |
| Fake packet injection | Working |
| Packet parsing during simulation | Working for the supplied test packets |
| In-memory worker-state population | Working |
| REST endpoints returning data | Working |
| Distinct status-summary endpoint | Pending |
| Port 8080 process management | Requires verification |
| Real receiver Nano serial integration | Not yet verified |

## 6. Next Steps

The recommended order of work is:

1. Review `StateManager.java` and `SensorPacket.java`.
2. Define the fields and rules required for a status summary.
3. Implement `/api/status` without changing the existing workers and events endpoints unnecessarily.
4. Verify each endpoint's response structure and behavior.
5. Ensure only one backend instance is running.
6. Replace or disable fake data injection when integrating real USB serial packets from the receiver Nano.

## Conclusion

The original empty-response problem was caused by an empty `FakeDataRunner.java` file. Implementing the runner populated the in-memory state manager with three valid test packets, allowing the backend APIs to return data.

The remaining API issue concerns endpoint design rather than the original initialization failure. The workers and status endpoints currently share the same implementation, while the events endpoint returns packet history. These responsibilities should be separated before the frontend integration is finalized.