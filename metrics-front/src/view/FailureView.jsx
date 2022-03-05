import React, { useState, useEffect } from 'react';
import TimelineChart from '../components/Chart/TimelineChart.jsx';
import { Table, Form, Container, Button, Row, Col } from 'react-bootstrap';
import InformationTable from '../components/Table/InformationTable.jsx';

const SummarizeView = () => {
    const[series, setSeries] = useState([]);
    const[counts, setCounts] = useState([]);
    const[tableData, setTableData] = useState([]);
    const[tableHeaders, setTableHeaders] = useState([]);
    const[selectedTimeline, setSelectedTimeline] = useState("WEEK");
    const[refresh, setRefresh] = useState(false);
    const url = `http://localhost:8080/v1/metrics/api?_timeline=${selectedTimeline}`;

    useEffect(() => {
      fetch(`${url}&_transaction=FAILURE_AVERAGE`)
        .then(response => response.json())
        .then(data => {
          let seriesData = [];
          let countsData = [];
          data.data.forEach(element => {
            seriesData.push([Date.parse(element.hour),element.average]);
            countsData.push([Date.parse(element.hour),element.count]);
          });
          setSeries([{"name":"Average","data":seriesData}]);
          setCounts([{"name":"Count","data":countsData}]);
        });

      fetch(`${url}&_transaction=FAILURE_OPERATIONS`)
        .then(response => response.json())
        .then(data => setTableData(data.data));

        fetch(`${url}&_transaction=FAILURE_OPERATIONS`)
        .then(response => response.json())
        .then(data => {
          let columnData = [];
          for (let key of Object.keys(data.data[0])) {
            let sort = key === 'name' ? false : true;
            columnData.push({dataField: key, text: key, sort: sort});
          }
          setTableHeaders(columnData)

          data.data.forEach(d => d.averagetime = d.averagetime.toFixed(2));

          setTableData(data.data);
        });
      },[selectedTimeline, refresh]);

    const onChange = (e) => {
      setSelectedTimeline(e.target.value);
    }

    const refreshData = (e) => {
      setRefresh(refresh ? false : true);
    }

    return (
      <Container className="pt-4">
        <Container fluid className="mb-3">
          <Row>
            <Col xs={10}>
            <Form.Select aria-label="Timeline Selector" onChange={onChange}>
              <option value="WEEK">Last Week</option>
              <option value="DAY">Last 24 hours</option>
              <option value="HOUR">Past hour</option>
              <option value="MINUTE">Past minute</option>
            </Form.Select>
            </Col>
            <Col xs={2}>
              <div className="d-grid gap-2">
                <Button className="btn-block" variant="secondary" onClick={refreshData}>Refresh</Button>
              </div>
            </Col>
          </Row>
        </Container>
          
        <TimelineChart title={"Summarized Metrics"} data={series} height={350} yTitle={"Average Time"} unit={"ms."}/>
        <TimelineChart title={""} data={counts} height={150} yTitle={"Calls"} unit={' '}/>
        <InformationTable data={tableData} columns={tableHeaders} rows={10}/>
      </Container>
    );
}

export default SummarizeView;