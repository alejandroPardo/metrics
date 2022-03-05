import React, { useState, useEffect } from 'react';
import StackChart from '../components/Chart/StackChart.jsx';
import { Form, Container, Row, Col, Button } from 'react-bootstrap';
import InformationTable from '../components/Table/InformationTable.jsx'

const TransactionsView = () => {
    const[series, setSeries] = useState([]);
    const[categories, setCategories] = useState([]);
    const[tableData, setTableData] = useState([]);
    const[tableHeaders, setTableHeaders] = useState([]);
    const[selectedTimeline, setSelectedTimeline] = useState("DAY");
    const url = `http://localhost:8080/v1/metrics/api?_timeline=${selectedTimeline}`;
    const[refresh, setRefresh] = useState(false);

    useEffect(() => {
      fetch(`${url}&_transaction=TRANSACTIONS_AVERAGE`)
        .then(response => response.json())
        .then(data => {
          let seriesData = [];
          let categoriesData = [];
          data.data.keys.forEach(key => {
            seriesData.push({'name': key, 'data':[]});
          });

          data.data.values.forEach(element => {
            categoriesData.push(element.timestamp);
            seriesData.forEach(serie => {
              serie.data.push(element.values.hasOwnProperty(serie.name) ? element.values[serie.name] : 0);
            })            
          });
          setSeries(seriesData);
          setCategories(categoriesData);
      });

      fetch(`${url}&_transaction=TRANSACTIONS`)
        .then(response => response.json())
        .then(data => {
          let columnData = [];
          for (let key of Object.keys(data.data[0])) {
            if(key !== 'transactionUuid' && key !== 'metricUuid' && key !== 'transactionLevel' && key !== 'code') columnData.push({dataField: key, text: key, sort: true});
          }
          setTableHeaders(columnData)
          setTableData(data.data)
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
          <StackChart data={series} categories={categories}/>
          <InformationTable data={tableData} columns={tableHeaders} events={true} rows={15}/>
        </Container>
    );
}

export default TransactionsView;