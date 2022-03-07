<div id="top"></div>

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/alejandroPardo/metrics">
    <img src="metrics-front/public/logo.svg" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">Metrics App</h3>

  <p align="center">
    Basic React app with Spring-boot backend and PostgreSQL database to receive, store and publish metrics by week, day, hour and minute.
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
        <li><a href="#mock-metrics-generator">Mock metrics generator</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->

## About The Project

![Metrics Screen Shot][product-screenshot]

### Built With

- [React.js](https://reactjs.org/)
- [Bootstrap](https://getbootstrap.com)
- [Spring-Boot](https://spring.io/projects/spring-boot)
- [PostgreSQL](https://www.postgresql.org/)

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- GETTING STARTED -->

## Getting Started

Follow this instructions to execute this project locally. It is dockerized, so very little configuration is needed.

### Prerequisites

To execute the project all that is required is Docker.
Download your preferred Docker application and make sure docker-compose is available and you should have no problems.

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/alejandroPardo/metrics.git
   ```
2. Execute docker compose to launch the services
   ```sh
   docker-compose up
   ```
3. That's it!
   ```
   FrontEnd: http://localhost
   BackEnd: http://localhost:8080
   ```

### Mock metrics generator

The docker compose file has a parameter for a mock generator of metrics, it can be activated by setting to true the flag GENERATE_METRICS in the backend part of the docker-compose

```
GENERATE_METRICS: 'false'
```

It is deactivated by default.

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->

## Usage

This is a basic example app. Used mainly to test technologies and show data. It should't be used as is in any case.

It's composed of 3 view,

- A Summarized metrics view, showing the average time that all metrics required to execute in an instant of time, the amount of calls made in that same instant and a table showing the requests made in that period. The period can be defined in a select form at the top of the view

- A Failed metrics view, showing the average time that all failed metrics required to execute in an instant of time, the amount of failed requests made in that same instant and a table showing the failed requests made in that period. The period can be defined in a select form at the top of the view

- A Transactions view, showing all the transactions made in a period of time, with separations in each type of metric and a table with all the transactions. When clicking this transactions, a new modal shows the timeline for the metric selected, as well as information for each transaction in that metric.
![Transactions Screen Shot][transactions-screenshot]
<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTACT -->

## Contact

Your Name - [@alejandropardo](https://twitter.com/alejandropardo) - alejandro.pardo.r@gmail.com

Project Link: [https://github.com/alejandroPardo/metrics](https://github.com/alejandroPardo/metrics)

<p align="right">(<a href="#top">back to top</a>)</p>

[product-screenshot]: metrics-front/public/application.png
[transactions-screenshot]: metrics-front/public/transactions.png
