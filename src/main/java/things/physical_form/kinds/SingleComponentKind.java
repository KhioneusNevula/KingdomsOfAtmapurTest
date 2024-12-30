package things.physical_form.kinds;

import things.physical_form.IKind;
import things.physical_form.IKindSettings;
import things.physical_form.ISoma;
import things.physical_form.components.IComponentPart;

public class SingleComponentKind implements IKind {

	private String name;

	public SingleComponentKind(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public <R extends IComponentPart> ISoma<R> generate(IKindSettings settings) {
		// TODO Auto-generated method stub
		return null;
	}

}
