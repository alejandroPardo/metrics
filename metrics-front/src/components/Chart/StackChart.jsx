import React, { useState, useEffect } from "react";
import ReactApexChart from "react-apexcharts";

const StackChart = (props) => {
  const [series, setSeries] = useState([]);

  const [categories, setCategories] = useState([]);

  useEffect(() => {
    setSeries(props.data);
    setCategories(props.categories);
  }, [props]);

  const options = {
    chart: {
      type: "bar",
      height: 350,
      stacked: true,
      toolbar: {
        show: true,
      },
      zoom: {
        enabled: false,
      },
    },
    dataLabels: {
      enabled: false,
    },
    markers: {
      size: 0,
    },
    plotOptions: {
      bar: {
        horizontal: false,
        borderRadius: 10,
      },
    },
    xaxis: {
      type: "datetime",
      categories: categories,
    },
    legend: {
      position: "bottom",
      offsetY: 10,
    },
    fill: {
      opacity: 1,
    },
  };

  return (
    <div>
      <ReactApexChart
        options={options}
        series={series}
        type="bar"
        height={350}
      />
    </div>
  );
};

export default StackChart;
