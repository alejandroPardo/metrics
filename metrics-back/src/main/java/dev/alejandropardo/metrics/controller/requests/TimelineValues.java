package dev.alejandropardo.metrics.controller.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimelineValues {

	WEEK	("hour"),
	DAY		("hour"),
	HOUR	("minute"),
	MINUTE	("second");

	/** The name. */
	private final String truncate;
}