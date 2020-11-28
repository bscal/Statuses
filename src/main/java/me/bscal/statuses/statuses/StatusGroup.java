package me.bscal.statuses.statuses;

public enum StatusGroup
{

	BLEEDS(0),
	FRACTURE(1);

	public final int id;

	private StatusGroup(final int id)
	{
		this.id = id;
	}

}
