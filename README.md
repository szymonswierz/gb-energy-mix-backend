# GB Energy Mix Backend

Spring Boot backend for a Great Britain energy mix dashboard.

The application fetches electricity generation mix data from an external API, calculates daily average energy source percentages, and determines the optimal electric vehicle charging window based on the highest share of clean energy.

## Features

* Fetches Great Britain electricity generation mix data from an external API
* Calculates average daily energy mix for three days: today, tomorrow and the day after tomorrow
* Calculates the share of clean energy sources:

  * biomass
  * nuclear
  * hydro
  * solar
  * wind
* Finds the optimal EV charging window for a selected charging duration
* Supports charging durations from 1 to 6 full hours
* Allows the optimal charging window to start on one day and end on the next
* Provides REST API endpoints for the frontend application
* Includes unit tests for service logic and error handling

## Tech Stack

* Java
* Spring Boot
* Spring Web
* Spring Cloud OpenFeign
* Lombok
* JUnit 5
* Mockito
* Docker

## API Endpoints

### Get daily energy mix

```http
GET /api/v1/energy-mix-daily
```

Returns average energy source percentages and clean energy percentage for three days.

Example response:

```json
[
  {
    "date": "2026-06-14",
    "energySourcePercentages": [
      {
        "fuel": "wind",
        "percentage": 24.94
      },
      {
        "fuel": "solar",
        "percentage": 14.94
      }
    ],
    "cleanEnergyPercentage": 57.11
  }
]
```

### Get optimal charging window

```http
GET /api/v1/optimal-charging-window/{hours}
```

Returns the best charging window for the selected number of full hours.

Example:

```http
GET /api/v1/optimal-charging-window/3
```

Example response:

```json
{
  "from": "2026-06-14T10:30Z",
  "to": "2026-06-14T13:30Z",
  "averageCleanEnergyPercentage": 68.98
}
```

Times are returned in UTC.

## Error Handling

The backend returns structured error responses for invalid input, missing data and external API issues.

Example error response:

```json
{
  "message": "Hours must be between 1 and 6",
  "timestamp": "2026-06-14T10:00:00"
}
```

## Running Locally

Clone the repository:

```bash
git clone https://github.com/szymonswierz/gb-energy-mix-backend.git
cd gb-energy-mix-backend
```

Run the application:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The backend will be available at:

```http
http://localhost:8080
```

## Running Tests

```bash
./mvnw test
```

On Windows:

```bash
mvnw.cmd test
```

## Docker

Build the application:

```bash
./mvnw clean package
```

On Windows:

```bash
mvnw.cmd clean package
```

Build the Docker image:

```bash
docker build -t gb-energy-mix-backend .
```

Run the container:

```bash
docker run -p 8080:8080 gb-energy-mix-backend
```

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── dev.szymon.gbenergymixbackend
│   │       ├── energymix
│   │       ├── exception
│   │       ├── model
│   │       ├── neso
│   │       ├── nesoapi
│   │       └── response
│   └── resources
└── test
    └── java
```

## Status

Backend implementation is complete and ready to be connected with the React frontend.
