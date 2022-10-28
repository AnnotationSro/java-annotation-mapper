package sk.annotation.library.jam.processor.utils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.PackageElement;
import java.util.Iterator;

public class JdkPackageUtils {
    public static PackageElement getPackageElement(ProcessingEnvironment processingEnv, String pckParentName) {
        Iterator<? extends PackageElement> iterator = processingEnv.getElementUtils().getAllPackageElements(pckParentName).iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
}
