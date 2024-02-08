package com.grigoriliev.jsoup.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class JSoupUtil {

	public static Optional<Element> findSibling(
		Element element, Predicate<Element> findPredicate, Predicate<Element> stopPredicate
	) {
		return findSibling(element, findPredicate, el -> true, stopPredicate);
	}

	public static Optional<Element> findSibling(
		Element element,
		Predicate<Element> findPredicate,
		Predicate<Element> startPredicate,
		Predicate<Element> stopPredicate
	) {
		return findSiblings(element, findPredicate, startPredicate, stopPredicate).findFirst();
	}

	public static Stream<Element> findSiblings(
		Element element, Predicate<Element> findPredicate, Predicate<Element> stopPredicate
	) {
		return findSiblings(element, findPredicate, el -> true, stopPredicate);
	}

	public static Stream<Element> findSiblings(
		Element element,
		Predicate<Element> findPredicate,
		Predicate<Element> startPredicate,
		Predicate<Element> stopPredicate
	) {
		return Optional.ofNullable(element.parent()).map(Element::children).map(
			siblings -> find(
				IntStream.range(element.elementSiblingIndex() + 1, siblings.size())
					.mapToObj(siblings::get),
				findPredicate, startPredicate, stopPredicate
			)
		).orElse(Stream.empty());
	}

	public static Stream<Element> find(
		Stream<Element> elementStream,
		Predicate<Element> findPredicate,
		Predicate<Element> startPredicate,
		Predicate<Element> stopPredicate
	) {
		final boolean[] startedAry = new boolean[1];
		return elementStream.takeWhile(el -> !stopPredicate.test(el)).filter(
			el -> {
				if (startedAry[0]) {
					return findPredicate.test(el);
				} else if (startPredicate.test(el)) {
					startedAry[0] = true;
					return findPredicate.test(el);
				}
				return false;
			}
		);
	}

	public static Stream<Element> findSiblings(
		Element element, int count
	) {
		return Optional.ofNullable(element.parent()).map(Element::children).map(
			siblings -> IntStream.range(element.elementSiblingIndex() + 1, siblings.size())
				.mapToObj(siblings::get)
		).map(
			stream -> count == -1 ? stream : stream.limit(count)
		).orElse(Stream.empty());
	}

	public static Stream<Node> findSiblingNodes(
		Element element, int count
	) {
		return Optional.ofNullable(element.parent()).map(Element::childNodes).map(
			siblings -> IntStream.range(indexInList(element, siblings) + 1, siblings.size())
				.mapToObj(siblings::get)
		).map(
			stream -> count == -1 ? stream : stream.limit(count)
		).orElse(Stream.empty());
	}

	private static <N extends Node> int indexInList(Node search, List<N> nodes) {
		final int size = nodes.size();
		for (int i = 0; i < size; i++) {
			if (nodes.get(i) == search)
				return i;
		}
		return 0;
	}
}
