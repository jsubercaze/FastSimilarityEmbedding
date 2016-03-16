package fr.ujm.lhc.krr.fse.test.fingerprint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.junit.Assert;
import org.junit.Test;

import fr.ujm.lhc.krr.fse.hash.tree.hasher.Fingerprint;


public class TestMakeFingerprint {

	@Test
	public void test() {
		Fingerprint f = Fingerprint.makeFingerprint(4, 3);
		assertEquals("100", f.toString());
		f = Fingerprint.makeFingerprint(4, 4);
		assertEquals("0100", f.toString());
		try {
			f = Fingerprint.makeFingerprint(12, 3);
			Assert.fail("Exception not raised");
		} catch (OutOfRangeException e) {
			assertTrue(true);
		}

	}

}
