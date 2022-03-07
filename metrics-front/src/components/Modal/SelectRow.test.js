import { render, screen } from "@testing-library/react";
import SelectRow from "./SelectRow.jsx";

const tableData = {
  name: "DELETE v1/api/reports",
  timestamp: "2022-03-06T10:37:20",
  duration: 15228,
  description: "Response 200 OK",
  transactions: [
    {
      name: "Oracle",
      timestamp: "2022-03-06T10:37:27.276246",
      duration: 2896,
      type: "DEPENDENCY",
      value: "select reports metrics from schema.metrics",
    },
    {
      name: "LOG INFO",
      timestamp: "2022-03-06T10:37:30.172246",
      duration: 0,
      type: "TRACE",
      value: "INFO Successfully updated",
    },
    {
      name: "DELETE v1/api/reports",
      timestamp: "2022-03-06T10:37:30.172246",
      duration: 5819,
      type: "REQUEST",
      value: "Response Code 200",
    },
  ],
};

test("renders Select Row component selected 0", () => {
  render(<SelectRow content={tableData} selected={0} />);
  expect(screen.getByText("DELETE v1/api/reports")).toBeInTheDocument();
  expect(screen.getByText("Response 200 OK")).toBeInTheDocument();
});

test("renders Select Row component selected 1", () => {
  render(<SelectRow content={tableData} selected={1} />);
  expect(screen.getByText("Oracle")).toBeInTheDocument();
  expect(
    screen.getByText("DEPENDENCY select reports metrics from schema.metrics")
  ).toBeInTheDocument();
});
