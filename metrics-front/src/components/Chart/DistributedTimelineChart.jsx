import React, { useState, useEffect } from "react";
import ReactApexChart from "react-apexcharts";
import SelectRow from "../Modal/SelectRow.jsx";

const DistributedTimelineChart = (props) => {
  const [selected, setSelected] = useState(0);
  const [series, setSeries] = useState([]);
  const [tableData, setTableData] = useState({});

  useEffect(() => {
    const content = props.content;
    let data = [];
    let array = [];
    data.push({
      x: content.name,
      y: [
        new Date(content.timestamp).getTime(),
        new Date(content.timestamp).getTime() + content.duration + 1,
      ],
    });
    content.transactions.forEach((t) => {
      data.push({
        x: `${t.type} ${t.name}`,
        y: [
          new Date(t.timestamp).getTime(),
          new Date(t.timestamp).getTime() + t.duration + 100,
        ],
      });
    });
    array.push({ data: data });
    setSeries(array);
    setTableData(content);
  }, [props.content]);

  const options = {
    chart: {
      height: 350,
      type: "rangeBar",
      zoom: {
        enabled: false,
      },
      events: {
        click: function (event, chartContext, config) {
          setSelected(config.dataPointIndex);
        },
      },
    },
    plotOptions: {
      bar: {
        horizontal: true,
        distributed: true,
        dataLabels: {
          hideOverflowingLabels: true,
        },
      },
    },
    dataLabels: {
      enabled: true,
      formatter: function (val, opts) {
        return opts.w.globals.labels[opts.dataPointIndex];
      },
      style: {
        colors: ["#f3f4f5", "#fff"],
      },
    },
    xaxis: {
      type: "datetime",
    },
    yaxis: {
      show: false,
    },
    grid: {
      row: {
        colors: ["#f3f4f5", "#fff"],
        opacity: 1,
      },
    },
  };

  return (
    <div>
      <ReactApexChart
        options={options}
        series={series}
        type="rangeBar"
        height={props.height}
      />
      <SelectRow content={tableData} selected={selected} />
    </div>
  );
};

export default DistributedTimelineChart;
