import React, { useState, useEffect } from "react";
import BootstrapTable from "react-bootstrap-table-next";
import filterFactory from "react-bootstrap-table2-filter";
import InformationModal from "../Modal/InformationModal.jsx";
import paginationFactory from "react-bootstrap-table2-paginator";

const url = `http://localhost:8080/v1/metrics/api/`;

const InformationTable = (props) => {
  const [show, setShow] = useState(false);
  const [modalContent, setModalContent] = useState({});
  const hideModal = () => setShow(false);

  const [data, setData] = useState([]);
  const [columns, setColumns] = useState([{}]);

  useEffect(() => {
    setData(props.data);
    setColumns(props.columns);
  }, [columns, data, props]);

  const rowEvents = {
    onClick: (e, row, rowIndex) => {
      if (props.events) {
        fetch(`${url}${row.metricUuid}`)
          .then((response) => response.json())
          .then((data) => {
            let contents = data.data;
            setModalContent(contents);
            setShow(true);
          });
      }
    },
  };

  const pagination = paginationFactory({
    sizePerPage: props.rows,
    hideSizePerPage: true,
    showTotal: true,
  });

  return (
    <>
      <BootstrapTable
        striped
        hover
        condensed
        keyField="transactionUuid"
        data={data}
        columns={columns}
        bordered={false}
        filter={filterFactory()}
        rowEvents={rowEvents}
        noDataIndication="Table is Empty"
        pagination={pagination}
      />
      <InformationModal
        show={show}
        handleClose={hideModal}
        content={modalContent}
      />
    </>
  );
};

export default InformationTable;
